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
package brix.plugin.file.util;

import java.text.Normalizer;

/**
 * @author wickeria at gmail.com
 */
public class SeoUtils {
	private static final int MAX_URL_LENGTH = 30;

	public static String normalizeValue(String value) {
		return normalizeValue(value, MAX_URL_LENGTH);
	}

	// String decomposed = java.text.Normalizer.normalize(string,
	// Normalizer.Form.NFD);
	// return decomposed.replaceAll("p{InCombiningDiacriticalMarks}+", "");

	private static String normalizeValue(String value, int maxURLLength) {
		String inputValue = value;
		String ret = value;
		if (inputValue != null) {

			ret = Normalizer.normalize(inputValue.subSequence(0, inputValue.length()), Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]+",
					"");

			ret = ret.replaceAll("[^a-zA-Z0-9]", "-");

			ret = ret.replaceAll("-+", "-");

			if (ret.length() > maxURLLength) {
				ret = ret.substring(0, maxURLLength);
			}

			ret = ret.toLowerCase();
		}

		return ret;
	}
}