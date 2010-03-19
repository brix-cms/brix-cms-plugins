package brix.plugin.hierarchical.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.nodes.TitledNode;
import brix.plugin.site.SimpleCallback;
import brix.web.ContainerFeedbackPanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.util.validators.NodeNameValidator;

public abstract class CreateTitledNodePanel extends BrixGenericPanel<BrixNode> {
	private static final long serialVersionUID = 1L;

	private String name;
	private final HierarchicalPluginLocator pluginLocator;

	public CreateTitledNodePanel(String id, IModel<BrixNode> containerNodeModel, final String type, final SimpleCallback goBack, HierarchicalPluginLocator pluginLocator) {
		super(id, containerNodeModel);
		this.pluginLocator = pluginLocator;

		final String typeName = pluginLocator.getPlugin().getNodeEditorPluginForType(type).getName();
		add(new Label("typeName", typeName));

		Form<?> form = new Form<CreateTitledNodePanel>("form", new CompoundPropertyModel<CreateTitledNodePanel>(this));
		add(form);

		form.add(new ContainerFeedbackPanel("feedback", this));

		form.add(new SubmitLink("create") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				createNode(type, typeName);
			}
		});

		form.add(new Link<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				goBack.execute();
			}
		});

		final TextField<String> tf;
		form.add(tf = new TextField<String>("name"));
		tf.setRequired(true);
		tf.add(NodeNameValidator.getInstance());

	}

	@SuppressWarnings("deprecation")
	private void createNode(String type, String typeName) {
		final JcrNode parent = getModelObject();

		if (parent.hasNode(name)) {
			String error = getString("resourceExists", new Model<CreateTitledNodePanel>(CreateTitledNodePanel.this), "A " + typeName + " with that name already exists.  Please use a different name.");
			error(error);
		} else {
			JcrNode node = parent.addNode(name, getJcrPrimaryType());
			TitledNode titledNode = initializeNode(node);
			titledNode.setTitle(name);
			name = null;
			parent.save();
			selectNode(titledNode);
		}
	}

	protected void selectNode(BrixNode node) {
		pluginLocator.getPlugin().selectNode(this, node, true);
	}

	protected abstract String getJcrPrimaryType();
	protected abstract TitledNode initializeNode(JcrNode node);
}
