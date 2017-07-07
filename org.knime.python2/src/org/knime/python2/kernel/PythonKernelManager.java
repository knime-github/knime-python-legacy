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
package org.knime.python2.kernel;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.knime.code2.generic.ImageContainer;
import org.knime.core.data.DataTable;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.util.ThreadPool;
import org.knime.python2.port.PickledObject;

/**
 * Manages a python kernel including executing commands in separate threads and switching the underling kernel.
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class PythonKernelManager {

    private ThreadPool m_threadPool;

    private PythonKernel m_kernel;

    /**
     * Creates a manager that will start a new python kernel.
     *
     * @param kernelOptions all configurable options
     *
     * @throws IOException
     */
    public PythonKernelManager(final PythonKernelOptions kernelOptions) throws IOException {
        m_threadPool = new ThreadPool(8);
        m_kernel = new PythonKernel(kernelOptions);
    }

    /**
     * Returns the image with the given name.
     *
     * @param name Name of the image
     * @return The image
     * @throws IOException If an error occured
     */
    public synchronized ImageContainer getImage(final String name) throws IOException {
        return m_kernel.getImage(name);
    }

    /**
     * Put a {@link PickledObject} into the python workspace (asynchronous).
     *
     * @param name the name of the variable in the python workspace
     * @param object the {@link PickledObject}
     * @param responseHandler the response handler
     *
     */
    public synchronized void putObject(final String name, final PickledObject object,
        final PythonKernelResponseHandler<Void> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                try {
                    kernel.putObject(name, object);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(null, exception);
                }
            }
        });
    }

    /**
     * Get a {@link PickledObject} from the python workspace (asynchronous).
     *
     * @param name the name of the variable in the python workspace
     * @param responseHandler the response handler
     */
    public synchronized void getObject(final String name,
        final PythonKernelResponseHandler<PickledObject> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                PickledObject response = null;
                Exception exception = null;
                try {
                    response = kernel.getObject(name, null);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(response, exception);
                }
            }
        });
    }

    /**
     * Switches the underling python kernel and closes the old one.
     *
     * This can be used to shutdown an unresponsive kernel.
     *
     * @param kernelOptions all configurable options for the kernel
     * @throws IOException If an error occurs during creation of the new python kernel
     */
    public synchronized void switchToNewKernel(final PythonKernelOptions kernelOptions) throws IOException {
        m_kernel.close();
        m_kernel = new PythonKernel(kernelOptions);
    }

    /**
     * Execute the given source code.
     *
     * @param sourceCode The source code to execute
     * @param responseHandler Handler for the responded console output
     */
    public synchronized void execute(final String sourceCode,
        final PythonKernelResponseHandler<String[]> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                String[] response = null;
                Exception exception = null;
                try {
                    response = kernel.execute(sourceCode);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(response, exception);
                }
            }
        });
    }

    /**
     * Put the given flow variables into the workspace.
     *
     * @param name The name of the flow variables dict
     * @param flowVariables The flow variables
     * @param responseHandler Handler called after execution (response object is always null)
     */
    public synchronized void putFlowVariables(final String name, final Collection<FlowVariable> flowVariables,
        final PythonKernelResponseHandler<Void> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                try {
                    kernel.putFlowVariables(name, flowVariables);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(null, exception);
                }
            }
        });
    }

    /**
     * Put the given data into the python workspace.
     *
     * @param tableNames the variable names in the python workspace for the tables to put
     * @param tables the tables to put
     * @param variablesName the variable name in the python workspace for the flow variable dict
     * @param variables the flow variables to put
     * @param objectNames the variable names in the python workspace for the objects to put
     * @param objects the objects to put
     * @param responseHandler Handler called after execution (response object is always null)
     * @param executionMonitor an execution monitor for reporting progress
     * @param rowLimit the maximum number of rows to put into a single table chunk
     */
    public synchronized void putData(final String[] tableNames, final BufferedDataTable[] tables,
        final String variablesName, final Collection<FlowVariable> variables, final String[] objectNames,
        final PickledObject[] objects, final PythonKernelResponseHandler<Void> responseHandler,
        final ExecutionMonitor executionMonitor, final int rowLimit) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                try {
                    kernel.putFlowVariables(variablesName, variables);
                    for (int i = 0; i < objects.length; i++) {
                        kernel.putObject(objectNames[i], objects[i]);
                    }
                    for (int i = 0; i < tables.length; i++) {
                        kernel.putDataTable(tableNames[i], tables[i],
                            executionMonitor.createSubProgress(1 / (double)tables.length), rowLimit);
                    }
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(null, exception);
                }
            }
        });
    }

    /**
     * Get a {@link DataTable} from the workspace.
     *
     * @param name The name of the table to get
     * @param exec the calling node's execution context
     * @param responseHandler Handler for the responded result table
     * @param executionMonitor an execution monitor for reporting progress
     */
    public synchronized void getDataTable(final String name, final ExecutionContext exec,
        final PythonKernelResponseHandler<DataTable> responseHandler, final ExecutionMonitor executionMonitor) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                DataTable response = null;
                Exception exception = null;
                try {
                    response = kernel.getDataTable(name, exec, executionMonitor);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(response, exception);
                }
            }
        });
    }

    /**
     * Returns the list of all defined variables, functions, classes and loaded modules.
     *
     * Each variable map contains the fields 'name', 'type' and 'value'.
     *
     * @param responseHandler Handler for the responded list of variables
     */
    public synchronized void
    listVariables(final PythonKernelResponseHandler<List<Map<String, String>>> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, String>> response = null;
                Exception exception = null;
                try {
                    response = kernel.listVariables();
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(response, exception);
                }
            }
        });
    }

    /**
     * Resets the workspace of the python kernel.
     *
     * @param responseHandler Handler called after execution (response object is always null)
     */
    public synchronized void resetWorkspace(final PythonKernelResponseHandler<Void> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                try {
                    kernel.resetWorkspace();
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(null, exception);
                }
            }
        });
    }

    /**
     * Returns the list of possible auto completions to the given source at the given position.
     *
     * Each auto completion contains the fields 'name', 'type' and 'doc'.
     *
     * @param sourceCode The source code
     * @param line Cursor position (line)
     * @param column Cursor position (column)
     * @param responseHandler Handler for the responded possible auto completions
     */
    public synchronized void autoComplete(final String sourceCode, final int line, final int column,
        final PythonKernelResponseHandler<List<Map<String, String>>> responseHandler) {
        final PythonKernel kernel = m_kernel;
        runInThread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, String>> response = null;
                Exception exception = null;
                try {
                    response = kernel.autoComplete(sourceCode, line, column);
                } catch (final Exception e) {
                    exception = e;
                }
                if (kernel.equals(m_kernel)) {
                    responseHandler.handleResponse(response, exception);
                }
            }
        });
    }

    /**
     * Closes the underling python kernel.
     */
    public synchronized void close() {
        m_threadPool.shutdown();
        m_threadPool = new ThreadPool(8);
        m_kernel.close();
    }

    /**
     * Runs the given runnable in a separate thread.
     *
     * @param runnable The runnable to run
     */
    private void runInThread(final Runnable runnable) {
        try {
            m_threadPool.submit(runnable);
        } catch (final InterruptedException e) {
            //
        }
    }

    /**
     * Get the managed python kernel.
     *
     * @return the managed python kernel
     */
    public PythonKernel getKernel() {
        return m_kernel;
    }

}
