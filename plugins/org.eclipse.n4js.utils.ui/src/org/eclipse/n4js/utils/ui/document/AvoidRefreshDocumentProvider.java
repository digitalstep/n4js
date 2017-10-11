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
package org.eclipse.n4js.utils.ui.document;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;

/**
 * A specialized document provider that avoids a resource scheduling rule on refresh if the file in question is already
 * synchronized.
 */
public class AvoidRefreshDocumentProvider extends XtextDocumentProvider {

	@Override
	protected void refreshFile(IFile file, IProgressMonitor monitor) throws CoreException {
		if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
			super.refreshFile(file, monitor);
		}
	}

}
