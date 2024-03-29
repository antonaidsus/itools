/*
 * Copyright  2002,2004-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package edu.ucla.loni.ccb.itools.servlet;

import java.io.IOException;

/**
 * Specialized IOException that get thrown if SMPT's QUIT command fails.
 *
 * <p>This seems to happen with some version of MS Exchange that
 * doesn't respond with a 221 code immediately.  See <a
 * href="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=5273">Bug
 * report 5273</a>.</p>
 *
 */
public class ErrorInQuitException extends IOException {

    /**
     * Initialise from an IOException
     *
     * @param e the IO Exception.
     */
    public ErrorInQuitException(IOException e) {
        super(e.getMessage());
    }

}
