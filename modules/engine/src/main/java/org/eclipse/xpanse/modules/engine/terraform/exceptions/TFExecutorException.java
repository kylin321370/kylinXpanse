/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.engine.terraform.exceptions;

/**
 * Defines possible exceptions returned by Terraform execution.
 */
public class TFExecutorException extends RuntimeException {

    public TFExecutorException() {
        super("TFExecutor Exception");
    }

    public TFExecutorException(String message) {
        super("TFExecutor Exception:" + message);
    }

    public TFExecutorException(String message, Throwable ex) {
        super(message, ex);
    }

    /**
     * Exception thrown.
     *
     * @param cmd    command that was executed in Terraform.
     * @param output Output of the command execution.
     * @param ex     Type of the exception thrown.
     */
    public TFExecutorException(String cmd, String output, Throwable ex) {
        super("Executor Exception:\n"
                        + "\n** Cmd:\n" + cmd + "\n** Output:\n" + output,
                ex);
    }

    public TFExecutorException(String cmd, String output) {
        super("Executor Exception:\n"
                + "\n** Cmd:\n" + cmd + "\n** Output:\n" + output);
    }
}
