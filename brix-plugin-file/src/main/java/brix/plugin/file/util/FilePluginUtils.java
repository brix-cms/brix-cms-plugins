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

import java.util.Arrays;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.value.ValueMap;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;

/**
 * @author wickeria at gmail.com
 */
public class FilePluginUtils {

	public static ValueMap getResourceParameters(JcrNode jcrNode) {
		if (jcrNode != null) {
			ValueMap map = new ValueMap(String
					.format("ws=%s,id=%s", jcrNode.getSession().getWorkspace().getName(), jcrNode.getIdentifier()));
			return map;
		}
		return new ValueMap();
	}

	public static String getResourceURLParameters(JcrNode jcrNode) {
		ValueMap map = getResourceParameters(jcrNode);
		StringBuilder builder = new StringBuilder("?");
		for (Entry<String, Object> entry : map.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append("&");
		}
		return builder.toString();
	}

	/**
	 * 
	 * @param params
	 *            should look like for example this"?ws=brix_ws_d42f753d_4eec_47a4_9efc_e162946ef02f&id=03070f24-d9c5-4705-badd-0ba505fcad2b&"
	 * @return
	 */
	public static JcrNode getNodeFromParams(String params) {
		try {
			String ws = params.substring(params.indexOf("ws=") + 3, params.indexOf("&"));
			String id = params.substring(params.indexOf("id=") + 3, params.lastIndexOf("&"));
			return Brix.get().getCurrentSession(ws).getNodeByIdentifier(id);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getResourceTag(JcrNode jcrNode, Component component) {
		if (jcrNode != null && BrixFileNode.isFileNode(jcrNode)) {
			BrixFileNode fileNode = new BrixFileNode(jcrNode, jcrNode.getSession());
			if (isImage(fileNode)) {
				return "<img src=\"" + ((ServletWebRequest) component.getRequest()).getHttpServletRequest().getContextPath() + "/"
						+ component.urlFor(new ResourceReference("file")) + getResourceURLParameters(jcrNode) + "\"/>";
			} else {
				return "<a href=\"" + ((ServletWebRequest) component.getRequest()).getHttpServletRequest().getContextPath() + "/"
						+ component.urlFor(new ResourceReference("file")) + getResourceURLParameters(jcrNode) + "\">download</a>";
			}
		}
		return "";
	}

	public static String getResolution(BrixFileNode node) {
		if (isImage(node)) {
			ImageInfo ii = new ImageInfo();
			ii.setInput(node.getDataAsStream());
			if (!ii.check()) {
				return "???";
			}
			return ii.getWidth() + " x " + ii.getHeight() + " pixels, " + ii.getBitsPerPixel() + " bits per pixel";
		}
		return "";
	}

	public static boolean isImage(BrixFileNode fileNode) {
		if (fileNode != null && Arrays.asList(ImageInfo.MIME_TYPE_STRINGS).contains(fileNode.getMimeType())) {
			return true;
		}
		return false;
	}

}
