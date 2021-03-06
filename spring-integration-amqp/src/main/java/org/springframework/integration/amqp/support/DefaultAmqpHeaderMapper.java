/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.amqp.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.mapping.AbstractHeaderMapper;
import org.springframework.integration.mapping.support.JsonHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link AmqpHeaderMapper}.
 * <p>
 * By default this implementation will only copy AMQP properties (e.g. contentType) to and from
 * Spring Integration MessageHeaders. Any user-defined headers within the AMQP
 * MessageProperties will NOT be copied to or from an AMQP Message unless
 * explicitly identified via 'requestHeaderNames' and/or 'replyHeaderNames'
 * (see {@link AbstractHeaderMapper#setRequestHeaderNames(String[])}
 * and {@link AbstractHeaderMapper#setReplyHeaderNames(String[])}}
 * as well as 'mapped-request-headers' and 'mapped-reply-headers' attributes of the AMQP adapters).
 * If you need to copy all user-defined headers simply use wild-card character '*'.
 * <p>
 * Constants for the AMQP header keys are defined in {@link AmqpHeaders}.
 *
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Gary Russell
 * @author Artem Bilan
 * @author Stephane Nicoll
 * @since 2.1
 */
public class DefaultAmqpHeaderMapper extends AbstractHeaderMapper<MessageProperties> implements AmqpHeaderMapper {

	private static final List<String> STANDARD_HEADER_NAMES = new ArrayList<String>();

	static {
		STANDARD_HEADER_NAMES.add(AmqpHeaders.APP_ID);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.CLUSTER_ID);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.CONTENT_ENCODING);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.CONTENT_LENGTH);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.CONTENT_TYPE);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.CORRELATION_ID);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.DELAY);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.DELIVERY_MODE);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.DELIVERY_TAG);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.EXPIRATION);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.MESSAGE_COUNT);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.MESSAGE_ID);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.RECEIVED_DELAY);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.RECEIVED_DELIVERY_MODE);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.RECEIVED_EXCHANGE);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.RECEIVED_ROUTING_KEY);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.REDELIVERED);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.REPLY_TO);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.TIMESTAMP);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.TYPE);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.USER_ID);
		STANDARD_HEADER_NAMES.add(JsonHeaders.TYPE_ID);
		STANDARD_HEADER_NAMES.add(JsonHeaders.CONTENT_TYPE_ID);
		STANDARD_HEADER_NAMES.add(JsonHeaders.KEY_TYPE_ID);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.SPRING_REPLY_CORRELATION);
		STANDARD_HEADER_NAMES.add(AmqpHeaders.SPRING_REPLY_TO_STACK);
	}

	protected DefaultAmqpHeaderMapper(String[] requestHeaderNames, String[] replyHeaderNames) {
		super(AmqpHeaders.PREFIX, STANDARD_HEADER_NAMES, STANDARD_HEADER_NAMES);
		if (requestHeaderNames != null) {
			setRequestHeaderNames(requestHeaderNames);
		}
		if (replyHeaderNames != null) {
			setReplyHeaderNames(replyHeaderNames);
		}
	}

	/**
	 * Extract "standard" headers from an AMQP MessageProperties instance.
	 */
	@Override
	protected Map<String, Object> extractStandardHeaders(MessageProperties amqpMessageProperties) {
		Map<String, Object> headers = new HashMap<String, Object>();
		try {
			String appId = amqpMessageProperties.getAppId();
			if (StringUtils.hasText(appId)) {
				headers.put(AmqpHeaders.APP_ID, appId);
			}
			String clusterId = amqpMessageProperties.getClusterId();
			if (StringUtils.hasText(clusterId)) {
				headers.put(AmqpHeaders.CLUSTER_ID, clusterId);
			}
			String contentEncoding = amqpMessageProperties.getContentEncoding();
			if (StringUtils.hasText(contentEncoding)) {
				headers.put(AmqpHeaders.CONTENT_ENCODING, contentEncoding);
			}
			long contentLength = amqpMessageProperties.getContentLength();
			if (contentLength > 0) {
				headers.put(AmqpHeaders.CONTENT_LENGTH, contentLength);
			}
			String contentType = amqpMessageProperties.getContentType();
			if (StringUtils.hasText(contentType)) {
				headers.put(AmqpHeaders.CONTENT_TYPE, contentType);
			}
			String correlationId = amqpMessageProperties.getCorrelationId();
			if (StringUtils.hasText(contentType)) {
				headers.put(AmqpHeaders.CORRELATION_ID, correlationId);
			}
			MessageDeliveryMode receivedDeliveryMode = amqpMessageProperties.getReceivedDeliveryMode();
			if (receivedDeliveryMode != null) {
				headers.put(AmqpHeaders.RECEIVED_DELIVERY_MODE, receivedDeliveryMode);
			}
			long deliveryTag = amqpMessageProperties.getDeliveryTag();
			if (deliveryTag > 0) {
				headers.put(AmqpHeaders.DELIVERY_TAG, deliveryTag);
			}
			String expiration = amqpMessageProperties.getExpiration();
			if (StringUtils.hasText(expiration)) {
				headers.put(AmqpHeaders.EXPIRATION, expiration);
			}
			Integer messageCount = amqpMessageProperties.getMessageCount();
			if (messageCount != null && messageCount > 0) {
				headers.put(AmqpHeaders.MESSAGE_COUNT, messageCount);
			}
			String messageId = amqpMessageProperties.getMessageId();
			if (StringUtils.hasText(messageId)) {
				headers.put(AmqpHeaders.MESSAGE_ID, messageId);
			}
			Integer priority = amqpMessageProperties.getPriority();
			if (priority != null && priority > 0) {
				headers.put(IntegrationMessageHeaderAccessor.PRIORITY, priority);
			}
			Integer receivedDelay = amqpMessageProperties.getReceivedDelay();
			if (receivedDelay != null) {
				headers.put(AmqpHeaders.RECEIVED_DELAY, receivedDelay);
			}
			String receivedExchange = amqpMessageProperties.getReceivedExchange();
			if (StringUtils.hasText(receivedExchange)) {
				headers.put(AmqpHeaders.RECEIVED_EXCHANGE, receivedExchange);
			}
			String receivedRoutingKey = amqpMessageProperties.getReceivedRoutingKey();
			if (StringUtils.hasText(receivedRoutingKey)) {
				headers.put(AmqpHeaders.RECEIVED_ROUTING_KEY, receivedRoutingKey);
			}
			Boolean redelivered = amqpMessageProperties.isRedelivered();
			if (redelivered != null) {
				headers.put(AmqpHeaders.REDELIVERED, redelivered);
			}
			String replyTo = amqpMessageProperties.getReplyTo();
			if (replyTo != null) {
				headers.put(AmqpHeaders.REPLY_TO, replyTo);
			}
			Date timestamp = amqpMessageProperties.getTimestamp();
			if (timestamp != null) {
				headers.put(AmqpHeaders.TIMESTAMP, timestamp);
			}
			String type = amqpMessageProperties.getType();
			if (StringUtils.hasText(type)) {
				headers.put(AmqpHeaders.TYPE, type);
			}
			String userId = amqpMessageProperties.getReceivedUserId();
			if (StringUtils.hasText(userId)) {
				headers.put(AmqpHeaders.RECEIVED_USER_ID, userId);
			}

			for (String jsonHeader : JsonHeaders.HEADERS) {
				Object value = amqpMessageProperties.getHeaders().get(jsonHeader.replaceFirst(JsonHeaders.PREFIX, ""));
				if (value instanceof String && StringUtils.hasText((String) value)) {
					headers.put(jsonHeader, value);
				}
			}

		}
		catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("error occurred while mapping from AMQP properties to MessageHeaders", e);
			}
		}
		return headers;
	}

	/**
	 * Extract user-defined headers from an AMQP MessageProperties instance.
	 */
	@Override
	protected Map<String, Object> extractUserDefinedHeaders(MessageProperties amqpMessageProperties) {
		return amqpMessageProperties.getHeaders();
	}

	/**
	 * Maps headers from a Spring Integration MessageHeaders instance to the MessageProperties
	 * of an AMQP Message.
	 */
	@Override
	protected void populateStandardHeaders(Map<String, Object> headers, MessageProperties amqpMessageProperties) {
		populateStandardHeaders(null, headers, amqpMessageProperties);
	}

	/**
	 * Maps headers from a Spring Integration MessageHeaders instance to the MessageProperties
	 * of an AMQP Message.
	 */
	@Override
	protected void populateStandardHeaders(@Nullable Map<String, Object> allHeaders, Map<String, Object> headers,
			MessageProperties amqpMessageProperties) {
		String appId = getHeaderIfAvailable(headers, AmqpHeaders.APP_ID, String.class);
		if (StringUtils.hasText(appId)) {
			amqpMessageProperties.setAppId(appId);
		}
		String clusterId = getHeaderIfAvailable(headers, AmqpHeaders.CLUSTER_ID, String.class);
		if (StringUtils.hasText(clusterId)) {
			amqpMessageProperties.setClusterId(clusterId);
		}
		String contentEncoding = getHeaderIfAvailable(headers, AmqpHeaders.CONTENT_ENCODING, String.class);
		if (StringUtils.hasText(contentEncoding)) {
			amqpMessageProperties.setContentEncoding(contentEncoding);
		}
		Long contentLength = getHeaderIfAvailable(headers, AmqpHeaders.CONTENT_LENGTH, Long.class);
		if (contentLength != null) {
			amqpMessageProperties.setContentLength(contentLength);
		}
		String contentType = this.extractContentTypeAsString(headers);

		if (StringUtils.hasText(contentType)) {
			amqpMessageProperties.setContentType(contentType);
		}

		String correlationId = getHeaderIfAvailable(headers, AmqpHeaders.CORRELATION_ID, String.class);
		if (StringUtils.hasText(correlationId)) {
			amqpMessageProperties.setCorrelationId(correlationId);
		}

		Integer delay = getHeaderIfAvailable(headers, AmqpHeaders.DELAY, Integer.class);
		if (delay != null) {
			amqpMessageProperties.setDelay(delay);
		}
		MessageDeliveryMode deliveryMode = getHeaderIfAvailable(headers, AmqpHeaders.DELIVERY_MODE,
				MessageDeliveryMode.class);
		if (deliveryMode != null) {
			amqpMessageProperties.setDeliveryMode(deliveryMode);
		}
		Long deliveryTag = getHeaderIfAvailable(headers, AmqpHeaders.DELIVERY_TAG, Long.class);
		if (deliveryTag != null) {
			amqpMessageProperties.setDeliveryTag(deliveryTag);
		}
		String expiration = getHeaderIfAvailable(headers, AmqpHeaders.EXPIRATION, String.class);
		if (StringUtils.hasText(expiration)) {
			amqpMessageProperties.setExpiration(expiration);
		}
		Integer messageCount = getHeaderIfAvailable(headers, AmqpHeaders.MESSAGE_COUNT, Integer.class);
		if (messageCount != null) {
			amqpMessageProperties.setMessageCount(messageCount);
		}
		String messageId = getHeaderIfAvailable(headers, AmqpHeaders.MESSAGE_ID, String.class);
		if (StringUtils.hasText(messageId)) {
			amqpMessageProperties.setMessageId(messageId);
		}
		else if (allHeaders != null) {
			UUID id = getHeaderIfAvailable(allHeaders, MessageHeaders.ID, UUID.class);
			if (id != null) {
				amqpMessageProperties.setMessageId(id.toString());
			}
		}
		Integer priority = getHeaderIfAvailable(headers, IntegrationMessageHeaderAccessor.PRIORITY, Integer.class);
		if (priority != null) {
			amqpMessageProperties.setPriority(priority);
		}
		String receivedExchange = getHeaderIfAvailable(headers, AmqpHeaders.RECEIVED_EXCHANGE, String.class);
		if (StringUtils.hasText(receivedExchange)) {
			amqpMessageProperties.setReceivedExchange(receivedExchange);
		}
		String receivedRoutingKey = getHeaderIfAvailable(headers, AmqpHeaders.RECEIVED_ROUTING_KEY, String.class);
		if (StringUtils.hasText(receivedRoutingKey)) {
			amqpMessageProperties.setReceivedRoutingKey(receivedRoutingKey);
		}
		Boolean redelivered = getHeaderIfAvailable(headers, AmqpHeaders.REDELIVERED, Boolean.class);
		if (redelivered != null) {
			amqpMessageProperties.setRedelivered(redelivered);
		}
		String replyTo = getHeaderIfAvailable(headers, AmqpHeaders.REPLY_TO, String.class);
		if (replyTo != null) {
			amqpMessageProperties.setReplyTo(replyTo);
		}
		Date timestamp = getHeaderIfAvailable(headers, AmqpHeaders.TIMESTAMP, Date.class);
		if (timestamp != null) {
			amqpMessageProperties.setTimestamp(timestamp);
		}
		else if (allHeaders != null) {
			Long ts = getHeaderIfAvailable(allHeaders, MessageHeaders.TIMESTAMP, Long.class);
			if (ts != null) {
				amqpMessageProperties.setTimestamp(new Date(ts));
			}
		}
		String type = getHeaderIfAvailable(headers, AmqpHeaders.TYPE, String.class);
		if (type != null) {
			amqpMessageProperties.setType(type);
		}
		String userId = getHeaderIfAvailable(headers, AmqpHeaders.USER_ID, String.class);
		if (StringUtils.hasText(userId)) {
			amqpMessageProperties.setUserId(userId);
		}

		Map<String, String> jsonHeaders = new HashMap<String, String>();

		for (String jsonHeader : JsonHeaders.HEADERS) {
			Object value = getHeaderIfAvailable(headers, jsonHeader, Object.class);
			if (value != null) {
				headers.remove(jsonHeader);
				if (value instanceof Class<?>) {
					value = ((Class<?>) value).getName();
				}
				jsonHeaders.put(jsonHeader.replaceFirst(JsonHeaders.PREFIX, ""), value.toString());
			}
		}

		/*
		 * If the MessageProperties already contains JsonHeaders, don't overwrite them here because they were
		 * set up by a message converter.
		 */
		if (!amqpMessageProperties.getHeaders().containsKey(JsonHeaders.TYPE_ID.replaceFirst(JsonHeaders.PREFIX, ""))) {
			amqpMessageProperties.getHeaders().putAll(jsonHeaders);
		}

		String replyCorrelation = getHeaderIfAvailable(headers, AmqpHeaders.SPRING_REPLY_CORRELATION, String.class);
		if (StringUtils.hasLength(replyCorrelation)) {
			amqpMessageProperties.setHeader("spring_reply_correlation", replyCorrelation);
		}
		String replyToStack = getHeaderIfAvailable(headers, AmqpHeaders.SPRING_REPLY_TO_STACK, String.class);
		if (StringUtils.hasLength(replyToStack)) {
			amqpMessageProperties.setHeader("spring_reply_to", replyToStack);
		}
	}

	@Override
	protected void populateUserDefinedHeader(String headerName, Object headerValue,
			MessageProperties amqpMessageProperties) {
		// do not overwrite an existing header with the same key
		// TODO: do we need to expose a boolean 'overwrite' flag?
		if (!amqpMessageProperties.getHeaders().containsKey(headerName)
				&& !AmqpHeaders.CONTENT_TYPE.equals(headerName)) {
			amqpMessageProperties.setHeader(headerName, headerValue);
		}
	}

	/**
	 * Will extract Content-Type from MessageHeaders and convert it to String if possible
	 * Required since Content-Type can be represented as org.springframework.util.MimeType.
	 *
	 */
	private String extractContentTypeAsString(Map<String, Object> headers) {
		String contentTypeStringValue = null;

		Object contentType = getHeaderIfAvailable(headers, AmqpHeaders.CONTENT_TYPE, Object.class);

		if (contentType != null) {
			String contentTypeClassName = contentType.getClass().getName();

			if (contentType instanceof MimeType) {
				contentTypeStringValue = contentType.toString();
			}
			else if (contentType instanceof String) {
				contentTypeStringValue = (String) contentType;
			}
			else {
				if (logger.isWarnEnabled()) {
					logger.warn("skipping header '" + AmqpHeaders.CONTENT_TYPE +
							"' since it is not of expected type [" + contentTypeClassName + "]");
				}
			}
		}
		return contentTypeStringValue;
	}

	@Override
	public Map<String, Object> toHeadersFromRequest(MessageProperties source) {
		Map<String, Object> headersFromRequest = super.toHeadersFromRequest(source);
		addConsumerMetadata(source, headersFromRequest);
		return headersFromRequest;
	}

	private void addConsumerMetadata(MessageProperties messageProperties, Map<String, Object> headers) {
		String consumerTag = messageProperties.getConsumerTag();
		if (consumerTag != null) {
			headers.put(AmqpHeaders.CONSUMER_TAG, consumerTag);
		}
		String consumerQueue = messageProperties.getConsumerQueue();
		if (consumerQueue != null) {
			headers.put(AmqpHeaders.CONSUMER_QUEUE, consumerQueue);
		}
	}

	/**
	 * Construct a default inbound header mapper.
	 * @return the mapper.
	 * @see #inboundRequestHeaders()
	 * @see #inboundReplyHeaders()
	 * @since 4.3
	 */
	public static DefaultAmqpHeaderMapper inboundMapper() {
		return new DefaultAmqpHeaderMapper(inboundRequestHeaders(), inboundReplyHeaders());
	}

	/**
	 * Construct a default outbound header mapper.
	 * @return the mapper.
	 * @see #outboundRequestHeaders()
	 * @see #outboundReplyHeaders()
	 * @since 4.3
	 */
	public static DefaultAmqpHeaderMapper outboundMapper() {
		return new DefaultAmqpHeaderMapper(outboundRequestHeaders(), outboundReplyHeaders());
	}

	/**
	 * @return the default request headers for an inbound mapper.
	 * @since 4.3
	 */
	public static String[] inboundRequestHeaders() {
		return new String[] { "*" };
	}

	/**
	 * @return the default reply headers for an inbound mapper.
	 * @since 4.3
	 */
	public static String[] inboundReplyHeaders() {
		return safeOutboundHeaders();
	}

	/**
	 * @return the default request headers for an outbound mapper.
	 * @since 4.3
	 */
	public static String[] outboundRequestHeaders() {
		return safeOutboundHeaders();
	}

	/**
	 * @return the default reply headers for an outbound mapper.
	 * @since 4.3
	 */
	public static String[] outboundReplyHeaders() {
		return new String[] { "*" };
	}

	private static String[] safeOutboundHeaders() {
		return new String[] { "!x-*", "*" };
	}

}
