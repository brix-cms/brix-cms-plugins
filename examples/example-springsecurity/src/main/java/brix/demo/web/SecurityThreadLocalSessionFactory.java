/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brix.demo.web;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import brix.jcr.ThreadLocalSessionFactory;

/**
 * @author wickeria at gmail.com
 */
public class SecurityThreadLocalSessionFactory extends ThreadLocalSessionFactory {

    public SecurityThreadLocalSessionFactory(Repository repository, Credentials credentials) {
        super(repository, credentials);
    }

    @Override
    protected Credentials getCredentials() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return super.getCredentials();
        }
        String pass = (String) auth.getCredentials();
        Credentials credentials = new SimpleCredentials(auth.getName(), pass != null ? pass
                .toCharArray() : new char[0]);
        return credentials;
    }

}
