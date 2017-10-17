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
package org.eclipse.n4js.postprocessing;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.n4js.n4JS.Script;
import org.eclipse.n4js.projectModel.IN4JSProject;
import org.eclipse.n4js.resource.N4JSResource;
import org.eclipse.n4js.ts.scoping.builtin.N4Scheme;
import org.eclipse.n4js.ts.types.TModule;
import org.eclipse.n4js.utils.resources.ExternalProject;

import com.google.inject.Singleton;

/**
 *
 */
/**
 * Inject this somewhere and invoke method {@link #investigate()} to start gathering statistics.
 */
@Singleton
@SuppressWarnings("javadoc")
public class TEMP {

	public void investigate(N4JSResource res) {
		final ResourceSet resSet = res.getResourceSet();
		final int countTotal = resSet.getResources().size();
		final int countBuiltIn = countN4JSResourcesBuiltIn(resSet);
		final int countLoadedFromAST = countN4JSResourcesLoadedFromAST(resSet) - 1; // do not count 'res' itself
		final int countLoadedFromIndex = countN4JSResourcesLoadedFromIndex(resSet);
		System.out.printf("%-50s   %3d   (%3d = %3d + %3d + %3d)",
				res.getURI().lastSegment(),
				countTotal,
				countLoadedFromAST + countLoadedFromIndex + countBuiltIn,
				countLoadedFromAST,
				countLoadedFromIndex,
				countBuiltIn);
		System.out.println();
	}

	private int countN4JSResourcesBuiltIn(ResourceSet resSet) {
		int n = 0;
		for (Resource res : resSet.getResources()) {
			if (isBuiltInResource(res)) {
				n++;
			}
		}
		return n;
	}

	private int countN4JSResourcesLoadedFromAST(ResourceSet resSet) {
		int n = 0;
		for (Resource res : resSet.getResources()) {
			if (!isBuiltInResource(res)) {
				if (res instanceof N4JSResource) {
					final N4JSResource resCasted = (N4JSResource) res;
					final Script script = resCasted.getScript();
					if (script != null && !script.eIsProxy()) {
						n++;
					}
				}
			}
		}
		return n;
	}

	private int countN4JSResourcesLoadedFromIndex(ResourceSet resSet) {
		int n = 0;
		for (Resource res : resSet.getResources()) {
			if (!isBuiltInResource(res)) {
				if (res instanceof N4JSResource) {
					final N4JSResource resCasted = (N4JSResource) res;
					final Script script = resCasted.getScript();
					final TModule module = resCasted.getModule();
					if (script != null && script.eIsProxy() && module != null && !module.eIsProxy()) {
						n++;
					}
				}
			}
		}
		return n;
	}

	private boolean isBuiltInResource(Resource res) {
		return N4Scheme.isResourceWithN4Scheme(res);
	}

	private boolean isManagedByLibraryManager(IN4JSProject project) {
		try {
			IProject eclipseProject = (IProject) project.getClass().getMethod("getProject").invoke(project);
			return eclipseProject instanceof ExternalProject;
		} catch (Throwable th) {
			return false;
		}
	}
}
