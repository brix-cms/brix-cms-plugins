package org.brixcms.plugin.content.blog.post.admin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.post.PostNode.State;
import org.brixcms.plugin.content.blog.post.PostNode.Visibility;
import org.brixcms.plugin.content.blog.post.admin.editor.PostEditorFactory;
import org.brixcms.plugin.content.blog.post.admin.resource.PostResourcesPanel;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorConfiguration;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.model.ModelBuffer;
import org.brixcms.web.reference.Reference;
import org.brixcms.web.tree.NodeFilter;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public abstract class PostEditTab extends BrixGenericPanel<PostNode> {
    private final MarkupContainer contentEditorParent;
    private final IModel<String> contentEditorModel;
    private String currentContentEditorFactory;

    public PostEditTab(String id, final IModel<PostNode> nodeModel) {
        super(id, nodeModel);

        Brix brix = getModelObject().getBrix();
        Form<Void> form = new Form<Void>("form");
        add(form);

        final ModelBuffer adapter = new ModelBuffer(nodeModel);

        form.add(new TextField<String>("title", adapter.forProperty("title")).setRequired(true));
        form.add(new TextField<String>("name", adapter.forProperty("name")).setRequired(true));
        form.add(new DateTimeField("publish", adapter.<Date> forProperty("publish")));
        form.add(new DropDownChoice<Visibility>("visibility", adapter.forProperty("visibility"), Arrays.asList(Visibility.values())));

        contentEditorModel = adapter.forProperty("dataAsString");
        contentEditorParent = form;

        Collection<PostEditorFactory> editorFactories = brix.getConfig().getRegistry().lookupCollection(PostEditorFactory.POINT);

        setupEditor(editorFactories.iterator().next().getClass().getName());

        RepeatingView contentEditors = new RepeatingView("contentEditors") {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return size() > 1;
            }
        };
        form.add(contentEditors);

        for (PostEditorFactory factory : editorFactories) {
            final String cn = factory.getClass().getName();
            contentEditors.add(new Button(contentEditors.newChildId(), new ResourceModel(factory.newStringLabelKey())) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSubmit() {
                    setupEditor(cn);
                }

                @Override
                public boolean isEnabled() {
                    return !cn.equals(currentContentEditorFactory);
                }
            });
        }

        form.add(new ContainerFeedbackPanel("feedback", this));
        form.add(new AjaxLink<Void>("showAvailableResources") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PostResourcesPanel resourcesPanel = new PostResourcesPanel("resources", nodeModel);
                resourcesPanel.setOutputMarkupId(true);
                form.get("resources").replaceWith(resourcesPanel);
                setVisible(false);
                target.add(resourcesPanel);
                target.add(this);
            }
        }.setOutputMarkupId(true));
        form.add(new EmptyPanel("resources").setOutputMarkupId(true));
        ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();
        conf.setDisplayFiles(true);
        conf.setAllowIndexedParameters(false);
        conf.setAllowQueryParameters(false);
        conf.setAllowURLEdit(false);
        conf.setNodeFilter(new NodeFilter() {

            @Override
            public boolean isNodeAllowed(BrixNode node) {
                return node instanceof BrixFileNode && ResourceUtils.isImage((BrixFileNode) node);
            }
        });
        conf.setRootNode(new IModel<BrixNode>() {
            @Override
            public BrixNode getObject() {
                return nodeModel.getObject().getResourcesFolder();
            }
        });
        IModel<Reference> model = adapter.forProperty("featuredImageReference");
        form.add(new ReferenceEditorPanel("featuredImageReference", model) {
            @Override
            protected IModel<String> newLabelModel() {
                return new PropertyModel<String>(getModel(), "nodeModel.object.name");
            }
        }.setConfiguration(conf));

        form.add(new Button("saveDraft") {

            @Override
            public void onSubmit() {
                save(adapter, nodeModel.getObject(), State.Draft);
                getSession().info(getString("content-plugin.status.saved"));
            }
        });

        form.add(new Button("savePublish") {

            @Override
            public void onSubmit() {
                save(adapter, nodeModel.getObject(), State.Published);
                getSession().info(getString("content-plugin.status.published"));
            }
        });

        form.add(new Link<Void>("cancel") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                getSession().info(getString("content-plugin.status.cancelled"));
                goBack();
            }

        });

    }

    private void save(final ModelBuffer adapter, PostNode node, State state) {
        node.checkout();
        adapter.apply();
        node.setState(state);
        if (state == State.Published && node.getPublish() == null) {
            node.setPublish(new Date());
        }
        node.save();
        node.checkin();
        ContentPlugin.get().selectNode(this, node, true);
        goBack();
    }

    private void setupEditor(String cn) {
        final Brix brix = getModelObject().getBrix();

        Collection<PostEditorFactory> factories = brix.getConfig().getRegistry().lookupCollection(PostEditorFactory.POINT);

        for (PostEditorFactory factory : factories) {
            if (factory.getClass().getName().equals(cn)) {
                contentEditorParent.addOrReplace(factory.newEditor("content", contentEditorModel));
                currentContentEditorFactory = factory.getClass().getName();
                return;
            }
        }

        throw new RuntimeException("Unknown markup editor factory class: " + cn);
    }

    abstract void goBack();

}