/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.integration.config;

import org.springframework.integration.endpoint.MessageEndpoint;
import org.springframework.integration.transformer.MethodInvokingTransformer;
import org.springframework.integration.transformer.TransformerEndpoint;

/**
 * Parser for the &lt;transformer/&gt; element.
 * 
 * @author Mark Fisher
 */
public class TransformerParser extends AbstractEndpointParser {

	@Override
	protected Class<? extends MessageEndpoint> getEndpointClass() {
		return TransformerEndpoint.class;
	}

	@Override
	protected Class<?> getMethodInvokingAdapterClass() {
		return MethodInvokingTransformer.class;
	}

}
