/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Sep 25, 2014 (Patrick Winter): created
 */
package org.knime.code2.generic.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.python2.Activator;

/**
 * Class managing source code templates that are made available via the org.knime.python2.sourcecodetemplates extension
 * point.
 *
 * @author Clemens von Schwerin, KNIME.com, Konstanz, Germany
 */

public class SourceCodeTemplatesExtensions {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SourceCodeTemplatesExtensions.class);

    private static List<File> templateFolders;

    /**
     * Read the paths to all folders containing source code templates available via the extension point and store them
     * internally.
     */
    public static void init() {
        templateFolders = new ArrayList<File>();
        final IConfigurationElement[] configs =
                Platform.getExtensionRegistry().getConfigurationElementsFor("org.knime.python2.sourcecodetemplates");
        for (final IConfigurationElement config : configs) {
            final String pluginId = config.getContributor().getName();
            final String path = config.getAttribute("path");
            final File folder = Activator.getFile(pluginId, path);
            if ((folder != null) && folder.isDirectory()) {
                templateFolders.add(folder);
            } else {
                LOGGER.warn("Could not find templates folder " + path + " in plugin " + pluginId);
            }
        }
    }

    /**
     * Provide a list of all folders containing source code templates available via the extension point
     *
     * @return list of template folders
     */
    public static List<File> getTemplateFolders() {
        return templateFolders;
    }

}
