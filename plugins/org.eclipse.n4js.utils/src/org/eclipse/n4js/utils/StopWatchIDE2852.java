/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.utils;

/**
 * Constants used for stop watch experiments during user story IDE2852.
 */
@SuppressWarnings("javadoc")
public class StopWatchIDE2852 {

	public static final StopWatch sw = new StopWatch();

	// @formatter:off
	public static final String FULL_BUILD						= "fullBuild";
	public static final String PARSER							= "  parser (only N4JS)";
	public static final String TYPES_BUILDER					= "  types builder";
	public static final String POST_PROCESSING					= "  post-processing";
	public static final String INF_CTX							= "    infCtx";
	public static final String STRUCT_SUBTYPE_CHECK				= "    structural subtype check";
	public static final String STRUCT_SUBTYPE_CHECK_IN_INF_CTX	= "      structural subtype check (in infCtx)";
	public static final String VALIDATION						= "  validation";
	public static final String FLOW_GRAPH_VALIDATION			= "    flowGraphValidation";
	public static final String TRANSPILER						= "  transpiler";
}
