package org.springframework.web.servlet.mvc.method;

import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Request mapping information. A composite for the following conditions:
 * <ol>
 * <li>{@link PathPatternsRequestCondition} with parsed {@code PathPatterns} or
 * {@link PatternsRequestCondition} with String patterns via {@code PathMatcher}
 * <li>{@link RequestMethodsRequestCondition}
 * <li>{@link ParamsRequestCondition}
 * <li>{@link HeadersRequestCondition}
 * <li>{@link ConsumesRequestCondition}
 * <li>{@link ProducesRequestCondition}
 * <li>{@code RequestCondition} (optional, custom request condition)
 * </ol>
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {

	private static final PathPatternsRequestCondition EMPTY_PATH_PATTERNS = new PathPatternsRequestCondition();

	private static final PatternsRequestCondition EMPTY_PATTERNS = new PatternsRequestCondition();

	private static final RequestMethodsRequestCondition EMPTY_REQUEST_METHODS = new RequestMethodsRequestCondition();

	private static final ParamsRequestCondition EMPTY_PARAMS = new ParamsRequestCondition();

	private static final HeadersRequestCondition EMPTY_HEADERS = new HeadersRequestCondition();

	private static final ConsumesRequestCondition EMPTY_CONSUMES = new ConsumesRequestCondition();

	private static final ProducesRequestCondition EMPTY_PRODUCES = new ProducesRequestCondition();

	private static final RequestConditionHolder EMPTY_CUSTOM = new RequestConditionHolder(null);


	@Nullable
	private final String name;

	@Nullable
	private final PathPatternsRequestCondition pathPatternsCondition;

	@Nullable
	private final PatternsRequestCondition patternsCondition;

	private final RequestMethodsRequestCondition methodsCondition;

	private final ParamsRequestCondition paramsCondition;

	private final HeadersRequestCondition headersCondition;

	private final ConsumesRequestCondition consumesCondition;

	private final ProducesRequestCondition producesCondition;

	private final RequestConditionHolder customConditionHolder;

	private final int hashCode;

	private final BuilderConfiguration options;


	/**
	 * Full constructor with a mapping name.
	 * @deprecated as of 5.3 in favor using {@link RequestMappingInfo.Builder} via
	 * {@link #paths(String...)}.
	 */
	@Deprecated
	public RequestMappingInfo(@Nullable String name, @Nullable PatternsRequestCondition patterns,
			@Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params,
			@Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes,
			@Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {

		this(name, null,
				(patterns != null ? patterns : EMPTY_PATTERNS),
				(methods != null ? methods : EMPTY_REQUEST_METHODS),
				(params != null ? params : EMPTY_PARAMS),
				(headers != null ? headers : EMPTY_HEADERS),
				(consumes != null ? consumes : EMPTY_CONSUMES),
				(produces != null ? produces : EMPTY_PRODUCES),
				(custom != null ? new RequestConditionHolder(custom) : EMPTY_CUSTOM),
				new BuilderConfiguration());
	}

	/**
	/**
	 * Create an instance with the given conditions.
	 * @deprecated as of 5.3 in favor using {@link RequestMappingInfo.Builder} via
	 * {@link #paths(String...)}.
	 */
	@Deprecated
	public RequestMappingInfo(@Nullable PatternsRequestCondition patterns,
			@Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params,
			@Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes,
			@Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {

		this(null, patterns, methods, params, headers, consumes, produces, custom);
	}

	/**
	 * Re-create a RequestMappingInfo with the given custom request condition.
	 * @deprecated since 5.3 in favor of using {@link #addCustomCondition(RequestCondition)}.
	 */
	@Deprecated
	public RequestMappingInfo(RequestMappingInfo info, @Nullable RequestCondition<?> customRequestCondition) {
		this(info.name, info.patternsCondition, info.methodsCondition, info.paramsCondition, info.headersCondition,
				info.consumesCondition, info.producesCondition, customRequestCondition);
	}

	private RequestMappingInfo(@Nullable String name,
			@Nullable PathPatternsRequestCondition pathPatternsCondition,
			@Nullable PatternsRequestCondition patternsCondition,
			RequestMethodsRequestCondition methodsCondition, ParamsRequestCondition paramsCondition,
			HeadersRequestCondition headersCondition, ConsumesRequestCondition consumesCondition,
			ProducesRequestCondition producesCondition, RequestConditionHolder customCondition,
			BuilderConfiguration options) {

		Assert.isTrue(pathPatternsCondition != null || patternsCondition != null,
				"Neither PathPatterns nor String patterns condition");

		this.name = (StringUtils.hasText(name) ? name : null);
		this.pathPatternsCondition = pathPatternsCondition;
		this.patternsCondition = patternsCondition;
		this.methodsCondition = methodsCondition;
		this.paramsCondition = paramsCondition;
		this.headersCondition = headersCondition;
		this.consumesCondition = consumesCondition;
		this.producesCondition = producesCondition;
		this.customConditionHolder = customCondition;
		this.options = options;

		this.hashCode = calculateHashCode(
				this.pathPatternsCondition, this.patternsCondition,
				this.methodsCondition, this.paramsCondition, this.headersCondition,
				this.consumesCondition, this.producesCondition, this.customConditionHolder);
	}


	/**
	 * Return the name for this mapping, or {@code null}.
	 */
	@Nullable
	public String getName() {
		return this.name;
	}

	/**
	 * Return the patterns condition in use when parsed patterns are
	 * {@link AbstractHandlerMapping#usesPathPatterns() enabled}.
	 * <p>This is mutually exclusive with {@link #getPatternsCondition()} such
	 * that when one returns {@code null} the other one returns an instance.
	 * @since 5.3
	 * @see #getActivePatternsCondition()
	 */
	@Nullable
	public PathPatternsRequestCondition getPathPatternsCondition() {
		return this.pathPatternsCondition;
	}

	/**
	 * Return the patterns condition when String pattern matching via
	 * {@link PathMatcher} is in use.
	 * <p>This is mutually exclusive with {@link #getPathPatternsCondition()}
	 * such that when one returns {@code null} the other one returns an instance.
	 */
	@Nullable
	public PatternsRequestCondition getPatternsCondition() {
		return this.patternsCondition;
	}

	/**
	 * Returns either {@link #getPathPatternsCondition()} or
	 * {@link #getPatternsCondition()} depending on which is not null.
	 * @since 5.3
	 */
	@SuppressWarnings("unchecked")
	public <T> RequestCondition<T> getActivePatternsCondition() {
		if (this.pathPatternsCondition != null) {
			return (RequestCondition<T>) this.pathPatternsCondition;
		}
		else if (this.patternsCondition != null) {
			return (RequestCondition<T>) this.patternsCondition;
		}
		else {
			// Already checked in the constructor...
			throw new IllegalStateException();
		}
	}

	/**
	 * Return the mapping paths that are not patterns.
	 * @since 5.3
	 */
	public Set<String> getDirectPaths() {
		RequestCondition<?> condition = getActivePatternsCondition();
		return (condition instanceof PathPatternsRequestCondition pprc ?
				pprc.getDirectPaths() : ((PatternsRequestCondition) condition).getDirectPaths());
	}

	/**
	 * Return the patterns for the {@link #getActivePatternsCondition() active}
	 * patterns condition as Strings.
	 * @since 5.3
	 */
	public Set<String> getPatternValues() {
		RequestCondition<?> condition = getActivePatternsCondition();
		return (condition instanceof PathPatternsRequestCondition pprc ?
				pprc.getPatternValues() : ((PatternsRequestCondition) condition).getPatterns());
	}

	/**
	 * Whether the request mapping has an empty URL path mapping.
	 * @since 6.0.10
	 */
	public boolean isEmptyMapping() {
		RequestCondition<?> condition = getActivePatternsCondition();
		return (condition instanceof PathPatternsRequestCondition pprc ?
				pprc.isEmptyPathMapping() : ((PatternsRequestCondition) condition).isEmptyPathMapping());
	}

	/**
	 * Return the HTTP request methods of this {@link RequestMappingInfo};
	 * or instance with 0 request methods (never {@code null}).
	 */
	public RequestMethodsRequestCondition getMethodsCondition() {
		return this.methodsCondition;
	}

	/**
	 * Return the "parameters" condition of this {@link RequestMappingInfo};
	 * or instance with 0 parameter expressions (never {@code null}).
	 */
	public ParamsRequestCondition getParamsCondition() {
		return this.paramsCondition;
	}

	/**
	 * Return the "headers" condition of this {@link RequestMappingInfo};
	 * or instance with 0 header expressions (never {@code null}).
	 */
	public HeadersRequestCondition getHeadersCondition() {
		return this.headersCondition;
	}

	/**
	 * Return the "consumes" condition of this {@link RequestMappingInfo};
	 * or instance with 0 consumes expressions (never {@code null}).
	 */
	public ConsumesRequestCondition getConsumesCondition() {
		return this.consumesCondition;
	}

	/**
	 * Return the "produces" condition of this {@link RequestMappingInfo};
	 * or instance with 0 produces expressions (never {@code null}).
	 */
	public ProducesRequestCondition getProducesCondition() {
		return this.producesCondition;
	}

	/**
	 * Return the "custom" condition of this {@link RequestMappingInfo}, or {@code null}.
	 */
	@Nullable
	public RequestCondition<?> getCustomCondition() {
		return this.customConditionHolder.getCondition();
	}

	/**
	 * Create a new instance based on the current one, also adding the given
	 * custom condition.
	 * @param customCondition the custom condition to add
	 * @since 5.3
	 */
	public RequestMappingInfo addCustomCondition(RequestCondition<?> customCondition) {
		return new RequestMappingInfo(this.name,
				this.pathPatternsCondition, this.patternsCondition,
				this.methodsCondition, this.paramsCondition, this.headersCondition,
				this.consumesCondition, this.producesCondition,
				new RequestConditionHolder(customCondition), this.options);
	}

	/**
	 * Combine "this" request mapping info (i.e. the current instance) with
	 * another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * @return a new request mapping info instance; never {@code null}
	 */
	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {
		String name = combineNames(other);

		PathPatternsRequestCondition pathPatterns =
				(this.pathPatternsCondition != null && other.pathPatternsCondition != null ?
						this.pathPatternsCondition.combine(other.pathPatternsCondition) : null);

		PatternsRequestCondition patterns =
				(this.patternsCondition != null && other.patternsCondition != null ?
						this.patternsCondition.combine(other.patternsCondition) : null);

		RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		ParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
		HeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
		ConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
		ProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
		RequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);

		return new RequestMappingInfo(name, pathPatterns, patterns,
				methods, params, headers, consumes, produces, custom, this.options);
	}

	@Nullable
	private String combineNames(RequestMappingInfo other) {
		if (this.name != null && other.name != null) {
			String separator = RequestMappingInfoHandlerMethodMappingNamingStrategy.SEPARATOR;
			return this.name + separator + other.name;
		}
		else if (this.name != null) {
			return this.name;
		}
		else {
			return other.name;
		}
	}

	/**
	 * Checks if all conditions in this request mapping info match the provided
	 * request and returns a potentially new request mapping info with conditions
	 * tailored to the current request.
	 * <p>For example the returned instance may contain the subset of URL
	 * patterns that match to the current request, sorted with best matching
	 * patterns on top.
	 * @return a new instance in case of a match; or {@code null} otherwise
	 */
	@Override
	@Nullable
	public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {
		RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
		if (methods == null) {
			return null;
		}
		ParamsRequestCondition params = this.paramsCondition.getMatchingCondition(request);
		if (params == null) {
			return null;
		}
		HeadersRequestCondition headers = this.headersCondition.getMatchingCondition(request);
		if (headers == null) {
			return null;
		}
		ConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(request);
		if (consumes == null) {
			return null;
		}
		ProducesRequestCondition produces = this.producesCondition.getMatchingCondition(request);
		if (produces == null) {
			return null;
		}
		PathPatternsRequestCondition pathPatterns = null;
		if (this.pathPatternsCondition != null) {
			pathPatterns = this.pathPatternsCondition.getMatchingCondition(request);
			if (pathPatterns == null) {
				return null;
			}
		}
		PatternsRequestCondition patterns = null;
		if (this.patternsCondition != null) {
			patterns = this.patternsCondition.getMatchingCondition(request);
			if (patterns == null) {
				return null;
			}
		}
		RequestConditionHolder custom = this.customConditionHolder.getMatchingCondition(request);
		if (custom == null) {
			return null;
		}
		return new RequestMappingInfo(this.name, pathPatterns, patterns,
				methods, params, headers, consumes, produces, custom, this.options);
	}

	/**
	 * Compares "this" info (i.e. the current instance) with another info in the
	 * context of a request.
	 * <p>Note: It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} to ensure they have
	 * conditions with content relevant to current request.
	 */
	@Override
	public int compareTo(RequestMappingInfo other, HttpServletRequest request) {
		int result;
		// Automatic vs explicit HTTP HEAD mapping
		if (HttpMethod.HEAD.matches(request.getMethod())) {
			result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
			if (result != 0) {
				return result;
			}
		}
		result = getActivePatternsCondition().compareTo(other.getActivePatternsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.paramsCondition.compareTo(other.getParamsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.headersCondition.compareTo(other.getHeadersCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.consumesCondition.compareTo(other.getConsumesCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.producesCondition.compareTo(other.getProducesCondition(), request);
		if (result != 0) {
			return result;
		}
		// Implicit (no method) vs explicit HTTP method mappings
		result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.customConditionHolder.compareTo(other.customConditionHolder, request);
		if (result != 0) {
			return result;
		}
		return 0;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof RequestMappingInfo that &&
				getActivePatternsCondition().equals(that.getActivePatternsCondition()) &&
				this.methodsCondition.equals(that.methodsCondition) &&
				this.paramsCondition.equals(that.paramsCondition) &&
				this.headersCondition.equals(that.headersCondition) &&
				this.consumesCondition.equals(that.consumesCondition) &&
				this.producesCondition.equals(that.producesCondition) &&
				this.customConditionHolder.equals(that.customConditionHolder)));
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@SuppressWarnings("ConstantConditions")
	private static int calculateHashCode(
			@Nullable PathPatternsRequestCondition pathPatterns, @Nullable PatternsRequestCondition patterns,
			RequestMethodsRequestCondition methods, ParamsRequestCondition params, HeadersRequestCondition headers,
			ConsumesRequestCondition consumes, ProducesRequestCondition produces, RequestConditionHolder custom) {

		return (pathPatterns != null ? pathPatterns : patterns).hashCode() * 31 +
				methods.hashCode() + params.hashCode() +
				headers.hashCode() + consumes.hashCode() + produces.hashCode() +
				custom.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		if (!this.methodsCondition.isEmpty()) {
			Set<RequestMethod> httpMethods = this.methodsCondition.getMethods();
			builder.append(httpMethods.size() == 1 ? httpMethods.iterator().next() : httpMethods);
		}

		// Patterns conditions are never empty and have "" (empty path) at least.
		builder.append(' ').append(getActivePatternsCondition());

		if (!this.paramsCondition.isEmpty()) {
			builder.append(", params ").append(this.paramsCondition);
		}
		if (!this.headersCondition.isEmpty()) {
			builder.append(", headers ").append(this.headersCondition);
		}
		if (!this.consumesCondition.isEmpty()) {
			builder.append(", consumes ").append(this.consumesCondition);
		}
		if (!this.producesCondition.isEmpty()) {
			builder.append(", produces ").append(this.producesCondition);
		}
		if (!this.customConditionHolder.isEmpty()) {
			builder.append(", and ").append(this.customConditionHolder);
		}
		builder.append('}');
		return builder.toString();
	}

	/**
	 * Return a builder to create a new RequestMappingInfo by modifying this one.
	 * @return a builder to create a new, modified instance
	 * @since 5.3.4
	 */
	public Builder mutate() {
		return new MutateBuilder(this);
	}


	/**
	 * Create a new {@code RequestMappingInfo.Builder} with the given paths.
	 * @param paths the paths to use
	 * @since 4.2
	 */
	public static Builder paths(String... paths) {
		return new DefaultBuilder(paths);
	}


	/**
	 * Defines a builder for creating a RequestMappingInfo.
	 * @since 4.2
	 */
	public interface Builder {

		/**
		 * Set the URL path patterns.
		 */
		Builder paths(String... paths);

		/**
		 * Set the request method conditions.
		 */
		Builder methods(RequestMethod... methods);

		/**
		 * Set the request param conditions.
		 */
		Builder params(String... params);

		/**
		 * Set the header conditions.
		 * <p>By default this is not set.
		 */
		Builder headers(String... headers);

		/**
		 * Set the consumes conditions.
		 */
		Builder consumes(String... consumes);

		/**
		 * Set the produces conditions.
		 */
		Builder produces(String... produces);

		/**
		 * Set the mapping name.
		 */
		Builder mappingName(String name);

		/**
		 * Set a custom condition to use.
		 */
		Builder customCondition(RequestCondition<?> condition);

		/**
		 * Provide additional configuration needed for request mapping purposes.
		 */
		Builder options(BuilderConfiguration options);

		/**
		 * Build the RequestMappingInfo.
		 */
		RequestMappingInfo build();
	}


	private static class DefaultBuilder implements Builder {

