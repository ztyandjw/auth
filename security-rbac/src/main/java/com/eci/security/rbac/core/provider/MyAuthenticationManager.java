package com.eci.security.rbac.core.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;


public class MyAuthenticationManager implements AuthenticationManager, MessageSourceAware,
		InitializingBean {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final Log logger = LogFactory.getLog(ProviderManager.class);

	// ~ Instance fields
	// ================================================================================================

	private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
	private List<AuthenticationProvider> providers = Collections.emptyList();
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private AuthenticationManager parent;
	private boolean eraseCredentialsAfterAuthentication = true;

	public MyAuthenticationManager(List<AuthenticationProvider> providers) {
		this(providers, null);
	}

	public MyAuthenticationManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
		Assert.notNull(providers, "providers list cannot be null");
		this.providers = providers;
		this.parent = parent;
		checkState();
	}

	// ~ Methods
	// ========================================================================================================

	public void afterPropertiesSet() throws Exception {
		checkState();
	}

	private void checkState() {
		if (parent == null && providers.isEmpty()) {
			throw new IllegalArgumentException(
					"A parent AuthenticationManager or a list "
							+ "of AuthenticationProviders is required");
		}
	}

	/**
	 * Attempts to authenticate the passed {@link Authentication} object.
	 * <p>
	 * The list of {@link AuthenticationProvider}s will be successively tried until an
	 * <code>AuthenticationProvider</code> indicates it is capable of authenticating the
	 * type of <code>Authentication</code> object passed. Authentication will then be
	 * attempted with that <code>AuthenticationProvider</code>.
	 * <p>
	 * If more than one <code>AuthenticationProvider</code> supports the passed
	 * <code>Authentication</code> object, the first one able to successfully
	 * authenticate the <code>Authentication</code> object determines the
	 * <code>result</code>, overriding any possible <code>AuthenticationException</code>
	 * thrown by earlier supporting <code>AuthenticationProvider</code>s.
	 * On successful authentication, no subsequent <code>AuthenticationProvider</code>s
	 * will be tried.
	 * If authentication was not successful by any supporting
	 * <code>AuthenticationProvider</code> the last thrown
	 * <code>AuthenticationException</code> will be rethrown.
	 *
	 * @param authentication the authentication request object.
	 *
	 * @return a fully authenticated object including credentials.
	 *
	 * @throws AuthenticationException if authentication fails.
	 */
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Class<? extends Authentication> toTest = authentication.getClass();
		AuthenticationException lastException = null;
		AuthenticationException parentException = null;
		Authentication result = null;
		Authentication parentResult = null;
		boolean debug = logger.isDebugEnabled();

		for (AuthenticationProvider provider : getProviders()) {
			if (!provider.supports(toTest)) {
				continue;
			}

			if (debug) {
				logger.debug("Authentication attempt using "
						+ provider.getClass().getName());
			}

			try {
				result = provider.authenticate(authentication);

				if (result != null) {
					copyDetails(authentication, result);
					break;
				}
			}
			catch (AccountStatusException e) {
				prepareException(e, authentication);
				// SEC-546: Avoid polling additional providers if auth failure is due to
				// invalid account status
				throw e;
			}
			catch (InternalAuthenticationServiceException e) {
				prepareException(e, authentication);
				throw e;
			}
			catch (AuthenticationException e) {
				lastException = e;
			}
		}

		if (result == null && parent != null) {
			// Allow the parent to try.
			try {
				result = parentResult = parent.authenticate(authentication);
			}
			catch (ProviderNotFoundException e) {
				// ignore as we will throw below if no other exception occurred prior to
				// calling parent and the parent
				// may throw ProviderNotFound even though a provider in the child already
				// handled the request
			}
			catch (AuthenticationException e) {
				lastException = parentException = e;
			}
		}

		if (result != null) {
			if (eraseCredentialsAfterAuthentication
					&& (result instanceof CredentialsContainer)) {
				// Authentication is complete. Remove credentials and other secret data
				// from authentication
				((CredentialsContainer) result).eraseCredentials();
			}

			// If the parent AuthenticationManager was attempted and successful than it will publish an AuthenticationSuccessEvent
			// This check prevents a duplicate AuthenticationSuccessEvent if the parent AuthenticationManager already published it
			if (parentResult == null) {
				eventPublisher.publishAuthenticationSuccess(result);
			}
			return result;
		}

		// Parent was null, or didn't authenticate (or throw an exception).

		if (lastException == null) {
			lastException = new ProviderNotFoundException(messages.getMessage(
					"ProviderManager.providerNotFound",
					new Object[] { toTest.getName() },
					"No AuthenticationProvider found for {0}"));
		}

		// If the parent AuthenticationManager was attempted and failed than it will publish an AbstractAuthenticationFailureEvent
		// This check prevents a duplicate AbstractAuthenticationFailureEvent if the parent AuthenticationManager already published it
		if (parentException == null) {
			prepareException(lastException, authentication);
		}

		throw lastException;
	}

	@SuppressWarnings("deprecation")
	private void prepareException(AuthenticationException ex, Authentication auth) {
		eventPublisher.publishAuthenticationFailure(ex, auth);
	}

	/**
	 * Copies the authentication details from a source Authentication object to a
	 * destination one, provided the latter does not already have one set.
	 *
	 * @param source source authentication
	 * @param dest the destination authentication object
	 */
	private void copyDetails(Authentication source, Authentication dest) {
		if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
			AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

			token.setDetails(source.getDetails());
		}
	}

	public List<AuthenticationProvider> getProviders() {
		return providers;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	public void setAuthenticationEventPublisher(
			AuthenticationEventPublisher eventPublisher) {
		Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
		this.eventPublisher = eventPublisher;
	}

	/**
	 * If set to, a resulting {@code Authentication} which implements the
	 * {@code CredentialsContainer} interface will have its
	 * {@link CredentialsContainer#eraseCredentials() eraseCredentials} method called
	 * before it is returned from the {@code authenticate()} method.
	 *
	 * @param eraseSecretData set to {@literal false} to retain the credentials data in
	 * memory. Defaults to {@literal true}.
	 */
	public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
		this.eraseCredentialsAfterAuthentication = eraseSecretData;
	}

	public boolean isEraseCredentialsAfterAuthentication() {
		return eraseCredentialsAfterAuthentication;
	}

	private static final class NullEventPublisher implements AuthenticationEventPublisher {
		public void publishAuthenticationFailure(AuthenticationException exception,
				Authentication authentication) {
		}

		public void publishAuthenticationSuccess(Authentication authentication) {
		}
	}
}
