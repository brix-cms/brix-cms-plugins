package brix.plugins.springsecurity;

import brix.jcr.api.JcrItem;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrValue;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IRenderable;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractLightWeightColumn;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.datagrid.DataGrid;
import org.apache.jackrabbit.value.StringValue;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.GrantedAuthority;

import javax.jcr.Value;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A Panel for displaying role requirements for a node.
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 12, 2009 9:22:34 AM
 */
public abstract class EditAccessPanel extends BrixGenericPanel<BrixNode> {
// ------------------------------ FIELDS ------------------------------

    DataGrid grid;

    @SpringBean
    private BrixUserProvider brixUserProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    public EditAccessPanel(String id, final IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        Form<?> form = new Form<Void>("form");
        BrixNode node = nodeModel.getObject();
        AbstractLightWeightColumn column = new AbstractLightWeightColumn("name", new ResourceModel("name")) {
            public IRenderable newCell(IModel rowModel) {
                return new IRenderable() {
                    public void render(IModel rowModel, Response response) {
                        response.write(((GrantedAuthority) rowModel.getObject()).getAuthority());
                    }
                };
            }
        };
        List<IGridColumn> columns = Arrays.asList((IGridColumn) new CheckBoxColumn("checkbox"), column.setInitialSize(300));
        IDataSource source = new IDataSource() {
            public IModel model(Object object) {
                return new Model((Serializable) object);
            }

            public void query(IQuery query, IQueryResult result) {
                List<GrantedAuthority> res = brixUserProvider.getAllAuthorities();
                Collections.sort(res, new Comparator<GrantedAuthority>() {
                    public int compare(GrantedAuthority o1, GrantedAuthority o2) {
                        return o1.getAuthority().compareTo(o2.getAuthority());
                    }
                });
                result.setItems(res.iterator());
                result.setTotalCount(res.size());
            }

            public void detach() {
            }
        };
        form.add(grid = new DataGrid("rolePicker", source, columns));

        grid.setClickRowToSelect(true);
        grid.setContentHeight(18, SizeUnit.EM);
        grid.setAllowSelectMultiple(true);

        JcrProperty propertyWrapper = null;
        if (node.hasProperty(UserPlugin.AUTH_GROUP_KEY)) {
            propertyWrapper = node.getProperty(UserPlugin.AUTH_GROUP_KEY);

            for (JcrValue value : propertyWrapper.getValues()) {
                GrantedAuthority module = brixUserProvider.getGrantedAuthorityByID(value.getString());
                grid.selectItem(new Model<GrantedAuthority>(module), true);
            }
        }

        form.add(new SubmitLink("save") {
            @Override
            public void onSubmit() {
                BrixNode node = nodeModel.getObject();
                Collection<IModel> perms = grid.getSelectedItems();
                Set<StringValue> values = new HashSet<StringValue>();
                for (IModel<GrantedAuthority> perm : perms) {
                    values.add(new StringValue(perm.getObject().getAuthority()));
                }
                if (values.size() > 0) {
                    node.setProperty(UserPlugin.AUTH_GROUP_KEY, values.toArray(new Value[values.size()]));
                } else if (node.hasProperty(UserPlugin.AUTH_GROUP_KEY)) {
                    JcrItem item = JcrItem.Wrapper.wrap(node.getProperty(UserPlugin.AUTH_GROUP_KEY), node.getSession());
                    item.remove();
                }
                node.save();
                getSession().info(getString("usersSaved"));
                goBack();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("editingCanceled"));
                goBack();
            }
        });

        add(form);
    }

    abstract void goBack();
}
