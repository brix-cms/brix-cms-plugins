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

package brix.tinymce;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class TinyMceEnabler extends Behavior
{

    private static final JavaScriptResourceReference MCE_JS = 
        new JavaScriptResourceReference(TinyMceEnabler.class, "tiny_mce/tiny_mce_src.js");
    private static final JavaScriptResourceReference ENABLER_JS = 
        new JavaScriptResourceReference(TinyMceEnabler.class, "enabler.js");

    @Override
    public void bind(Component component)
    {
        if (!(component instanceof TextArea))
        {
            throw new IllegalStateException(getClass().getName() + " can only be added to " +
                    TextArea.class.getName());
        }
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response)
    {
        response.renderJavaScript(mceInitializer(), "mceInitializer");
        response.renderJavaScriptReference(MCE_JS);
        response.renderJavaScriptReference(ENABLER_JS);
    }
    
    private CharSequence mceInitializer() 
    {
        return new StringBuilder()
        .append("var tinyMCEPreInit = {};\n")
        .append("tinyMCEPreInit.suffix=''; \n")
        .append("tinyMCEPreInit.base='").append(getMcePath()).append("/';")
        .toString();
    }

    private Object getMcePath() 
    {
        RequestCycle rc = RequestCycle.get();
        Url urlFor = rc.mapUrlFor(MCE_JS, null);
        List<String> segments = urlFor.getSegments();
        segments.remove(segments.size()-1);
        return rc.getOriginalResponse().encodeURL(rc.getUrlRenderer().renderUrl(urlFor));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag)
    {
        String clazz = (String)tag.getAttributes().get("class");
        clazz = (clazz == null) ? "" : clazz + " ";
        clazz += "mceEditor ";
        tag.put("class", clazz);
    }

}
