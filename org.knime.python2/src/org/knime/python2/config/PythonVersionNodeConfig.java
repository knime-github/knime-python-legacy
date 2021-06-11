/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 10, 2021 (marcel): created
 */
package org.knime.python2.config;

import java.util.Locale;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.context.NodeSettingsPersistable;
import org.knime.python2.PythonVersion;

/**
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 */
public final class PythonVersionNodeConfig implements NodeSettingsPersistable {

    private static final String DEFAULT_CFG_KEY_PYTHON_VERSION = "python_version";

    private final String m_configKey;

    private PythonVersion m_pythonVersion;

    /**
     * @param pythonVersion The initial Python version.
     */
    public PythonVersionNodeConfig(final PythonVersion pythonVersion) {
        this(DEFAULT_CFG_KEY_PYTHON_VERSION, pythonVersion);
    }

    /**
     * @param configKey The key for this config.
     * @param pythonVersion The initial Python version.
     */
    public PythonVersionNodeConfig(final String configKey, final PythonVersion pythonVersion) {
        m_configKey = configKey;
        m_pythonVersion = pythonVersion;
    }

    /**
     * @return The key of this configuration entry.
     */
    public String getConfigKey() {
        return m_configKey;
    }

    /**
     * @return The configured Python version.
     */
    public PythonVersion getPythonVersion() {
        return m_pythonVersion;
    }

    /**
     * @param pythonVersion The configured Python version.
     */
    public void setPythonVersion(final PythonVersion pythonVersion) {
        m_pythonVersion = pythonVersion;
    }

    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        settings.addString(m_configKey, getPythonVersion().getId());
    }

    @Override
    public void loadSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        final String pythonVersionString = settings.getString(m_configKey, getPythonVersion().getId());
        // Backward compatibility: older saved versions are all upper case.
        setPythonVersion(PythonVersion.fromId(pythonVersionString.toLowerCase(Locale.ROOT)));
    }
}
