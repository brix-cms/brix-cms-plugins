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

package brix.codepress;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.WicketEventJQueryResourceReference;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class CodePressEnabler extends Behavior
{
    private final String language;
    private final boolean lineNumbers;
    private Component owner;

    private static final JavaScriptResourceReference JS = new JavaScriptResourceReference(CodePressEnabler.class,
        "codepress.js");

    public CodePressEnabler(String language, boolean lineNumbers)
    {
        this.language = language;
        this.lineNumbers = lineNumbers;
    }

    @Override
    public void bind(Component component)
    {
        if (owner != null)
        {
            throw new IllegalStateException("This behavior is already bound to a component");
        }
        // TODO validate markup id of owner will not contain any funky
        // characters
        // because codepress creates a javascript variable whose name is the
        // html id attribute
        owner = component;
        owner.setOutputMarkupId(true);
    }

    @Override
    public void beforeRender(Component component)
    {
        if (component instanceof FormComponent)
        {
            ((FormComponent<?>)component).getForm().getRootForm().setOutputMarkupId(true);
        }
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response)
    {
        response.renderJavaScriptReference(JS);
        response.renderJavaScript(codePressInitializer(), "codePressInitializer");

        if (owner instanceof FormComponent)
        {
            response.renderJavaScriptReference(WicketEventReference.INSTANCE);
            final FormComponent<?> fc = (FormComponent<?>)component;
            final Form<?> form = fc.getForm().getRootForm();
            response.renderOnDomReadyJavaScript("Wicket.Event.add(document.getElementById('" +
                    form.getMarkupId() + "'), 'submit', function() { " + fc.getMarkupId() +
                    ".toggleEditor();});"));
        }
    }

    private String codePressInitializer() 
    {
      return new StringBuilder()
        .append("CodePress.path = '").append(getCodePressPath()).append("/';\n")
        .append(
            "if(window.attachEvent) {window.attachEvent('onload',CodePress.run);}" +
            "else {window.addEventListener('DOMContentLoaded',CodePress.run,false);}")
        .toString();
    }

    private String getCodePressPath() 
    {
      RequestCycle requestCycle = RequestCycle.get();
      Url urlFor = requestCycle.mapUrlFor(JS, null);
      List<String> segments = urlFor.getSegments();
      segments.remove(segments.size()-1);
      return requestCycle.getOriginalResponse().encodeURL(requestCycle.getUrlRenderer().renderUrl(urlFor));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag)
    {
        String clazz = (String)tag.getAttributes().get("class");
        clazz = (clazz == null) ? "" : clazz + " ";
        clazz += "codepress ";
        clazz += language;
        clazz += " linenumbers-";
        clazz += (lineNumbers) ? "on" : "off";
        tag.put("class", clazz);
    }
}
