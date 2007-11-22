// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.connections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.process.Element;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.cmd.ConnectionDeleteCommand;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.DynamicTabbedPropertySection;
import org.talend.designer.core.ui.views.properties.ComponentSettingsView;

/**
 * Graphical part of the connection of Gef. <br/>
 * 
 * $Id$
 * 
 */
public class ConnectionPart extends AbstractConnectionEditPart implements PropertyChangeListener {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
     */
    public void activate() {
        if (!isActive()) {
            super.activate();
            ((Element) getModel()).addPropertyChangeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
     */
    public void deactivate() {
        if (isActive()) {
            super.deactivate();
            ((Element) getModel()).removePropertyChangeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#setSelected(int)
     */
    public void setSelected(final int value) {
        super.setSelected(value);
        List cl = this.getChildren();
        for (int i = 0; i < cl.size(); i++) {
            if (((EditPart) cl.get(i)).getSelected() != value) {
                ((EditPart) cl.get(i)).setSelected(value);
            }
        }

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (DynamicTabbedPropertySection.DEBUG_TIME) {
            IViewPart view = page.findView(ComponentSettingsView.ID); //$NON-NLS-1$
            if (view != null) {
                ComponentSettingsView compSettings = (ComponentSettingsView) view;
                compSettings.setElement((Connection) getModel());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
        // Selection handle edit policy.
        // Makes the connection show a feedback, when selected by the user.
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        // Allows the removal of the connection model element
        installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {

            protected Command getDeleteCommand(GroupRequest request) {
                if (((Connection) getModel()).isReadOnly()) {
                    return null;
                }
                List<Connection> connectionList = new ArrayList<Connection>();
                for (int i = 0; i < request.getEditParts().size(); i++) {
                    if (request.getEditParts().get(i) instanceof ConnectionPart) {
                        connectionList.add(((Connection) ((ConnectionPart) request.getEditParts().get(i)).getModel()));
                    }
                }
                return new ConnectionDeleteCommand(connectionList);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
     */
    protected IFigure createFigure() {
        ConnectionFigure connection = new ConnectionFigure(((Connection) getModel()).getSourceNodeConnector()
                .getConnectionProperty(((Connection) getModel()).getLineStyle()));

        if (((Connection) getModel()).isActivate()) {
            ((ConnectionFigure) connection).setAlpha(-1);
        } else {
            ((ConnectionFigure) connection).setAlpha(Connection.ALPHA_VALUE);
        }
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(final PropertyChangeEvent event) {
        String property = event.getPropertyName();
        if (Connection.LINESTYLE_PROP.equals(property)) {
            ((ConnectionFigure) figure).setConnectionProperty(((Connection) getModel()).getSourceNodeConnector()
                    .getConnectionProperty(((Connection) getModel()).getLineStyle()));
            refreshChildren();
        }
        if (Connection.NAME.equals(property)) {
            refreshChildren();
        }

        if (property.equals(EParameterName.ACTIVATE.getName())) {
            if (((Connection) getModel()).isActivate()) {
                ((ConnectionFigure) figure).setAlpha(-1);
                ((ConnectionFigure) figure).repaint();
                refreshVisuals();
            } else {
                ((ConnectionFigure) figure).setAlpha(Connection.ALPHA_VALUE);
                ((ConnectionFigure) figure).repaint();
                refreshVisuals();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    protected List getModelChildren() {
        List<Element> elements;
        elements = new ArrayList<Element>();
        elements.add(((Connection) getModel()).getConnectionLabel());
        elements.add(((Connection) getModel()).getPerformance());
        if (((Connection) getModel()).getConnectionTrace() != null) {
            elements.add(((Connection) getModel()).getConnectionTrace());
        }
        return elements;
    }
}
