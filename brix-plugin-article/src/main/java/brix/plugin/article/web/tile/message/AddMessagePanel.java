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

package brix.plugin.article.web.tile.message;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

/**
 * @author wickeria at gmail.com
 */
public class AddMessagePanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AddMessagePanel(String id, IModel<BrixNode> model) {
	super(id, model);
	setOutputMarkupId(true);
	add(new FeedbackPanel("feedback"));
	Form<Message> form = new MessageForm("form");
	add(form);
	add(new AjaxSubmitLink("submit", form) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		target.addComponent(AddMessagePanel.this);
		onOk(target);
	    }

	    @Override
	    protected void onError(AjaxRequestTarget target, Form<?> form) {
		target.addComponent(AddMessagePanel.this);
	    }
	});
	add(new AjaxLink<Void>("close") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		onCancel(target);
	    }
	});
	add(new AjaxLink<Void>("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		onCancel(target);
	    }
	});

    }

    protected void onOk(AjaxRequestTarget target) {

    }

    protected void onCancel(AjaxRequestTarget target) {

    }

    private class MessageForm extends Form<Message> {
	private static final long serialVersionUID = 1L;

	private Message entry = new Message();

	public MessageForm(String id) {
	    super(id);
	    add(new TextField<String>("name", new PropertyModel<String>(this, "entry.name")).setRequired(true));
	    add(new TextField<String>("email", new PropertyModel<String>(this, "entry.email"))
		    .add(EmailAddressValidator.getInstance()));
	    add(new TextField<String>("title", new PropertyModel<String>(this, "entry.title")));
	    add(new TextArea<String>("message", new PropertyModel<String>(this, "entry.message")).setRequired(true));
	}

	@Override
	protected void onSubmit() {
	    onMessage(entry);
	    entry = new Message();
	}
    }

    protected void onMessage(Message message) {
	JcrNode tile = (JcrNode) getDefaultModelObject();
	long size = tile.getNodes("entry").getSize();
	JcrNode entry = tile.addNode("entry");
	if (message.getEmail() != null) {
	    entry.setProperty("email", message.getEmail());
	}
	if (message.getTitle() != null) {
	    entry.setProperty("title", message.getTitle());
	}
	entry.setProperty("index", size + 1);
	entry.setProperty("name", message.getName());
	entry.setProperty("message", message.getMessage());
	entry.setProperty("timestamp", System.currentTimeMillis());

	tile.getSession().save();
    }

}
