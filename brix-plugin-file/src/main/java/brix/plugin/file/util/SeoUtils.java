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

	public static final String PICTURE_PARAM_KEY = "image";

	public static final String SELECTED_PARAM_KEY = "selectedBy";

	public static final String RACE_PARAM_KEY = "race";
	public static final String RACE_EDIT_PARAM_KEY = "raceEdit";

	public static String normalizeValue(String value) {
		return normalizeValue(value, MAX_URL_LENGTH);
	}

	// TODO: toto bude asi lepsi:
	// String decomposed = java.text.Normalizer.normalize(string,
	// Normalizer.Form.NFD);
	// return decomposed.replaceAll("p{InCombiningDiacriticalMarks}+", "");

	private static String normalizeValue(String value, int maxURLLength) {
		String inputValue = value;
		String ret = value;
		if (inputValue != null) {
			/* odstran carky hacky */
			ret = Normalizer.normalize(inputValue.subSequence(0, inputValue.length()), Normalizer.Form.NFKD)
					.replaceAll("[^\\p{ASCII}]+", "");

			/* vsechny NE-alfanumericke znaky nahrad pomlckou */
			ret = ret.replaceAll("[^a-zA-Z0-9]", "-");

			/* opakujici pomlcky zgrupuj do jedne */
			ret = ret.replaceAll("-+", "-");

			/* orizni na velikost textfieldu, pokud to je nutne */
			if (ret.length() > maxURLLength) {
				ret = ret.substring(0, maxURLLength);
			}

			ret = ret.toLowerCase();
		}

		return ret;
	}

	/**
	 * use getLastParamId
	 */
	@Deprecated
	public static String separateParamId(String last) {
		return last.substring(last.lastIndexOf("-") + 1);
	}

	public static void main(String[] args) {
		// String str = "123-456";
		// System.out.println(getFirstParamId(str));
		// System.out.println(getLastParamId(str));
	}

	public static String getFirstParamId(String str) {
		return str.substring(0, str.indexOf("-"));
	}

	public static String getLastParamId(String last) {
		return last.substring(last.lastIndexOf("-") + 1);
	}
}