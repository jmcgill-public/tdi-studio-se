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
package org.talend.designer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.core.ui.ActiveProcessTracker;
import org.talend.designer.core.ui.MultiPageTalendEditor;
import org.talend.designer.core.ui.action.CreateProcess;
import org.talend.designer.core.ui.action.SaveJobBeforeRunAction;
import org.talend.designer.core.ui.editor.TalendEditor;
import org.talend.designer.core.ui.editor.TalendEditorPaletteFactory;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.editor.properties.GefEditorLabelProvider;
import org.talend.designer.core.ui.editor.properties.RepositoryValueUtils;
import org.talend.designer.core.ui.views.contexts.Contexts;
import org.talend.designer.runprocess.ProcessorUtilities;

/**
 * Detailled comment <br/>.
 * 
 * $Id: DesignerCoreService.java 1 2006-12-19 上午10:25:42 bqian
 * 
 */
public class DesignerCoreService implements IDesignerCoreService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getProcessFromProcessItem(org.talend.core.model.properties.ProcessItem)
     */
    public IProcess getProcessFromProcessItem(ProcessItem processItem) {
        Process process = null;
        process = new Process(processItem.getProperty());
        process.loadXmlFile(processItem.getProcess());

        return process;
    }

    public ILabelProvider getGEFEditorNodeLabelProvider() {
        return new GefEditorLabelProvider();
    }

    // used for generating HTML only
    /**
     * Constructs a new instance.
     */
    private RepositoryValueUtils repositoryValueUtils = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getQueriesMap()
     */
    public List<Map> getMaps() {
        if (repositoryValueUtils == null) {
            repositoryValueUtils = new RepositoryValueUtils();
        }
        List<Map> list = new ArrayList<Map>();
        list.add(repositoryValueUtils.getRepositoryConnectionItemMap());
        list.add(repositoryValueUtils.getRepositoryDBIdAndNameMap());
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getRepositoryAliasName(org.talend.core.model.properties.ConnectionItem)
     */
    public String getRepositoryAliasName(ConnectionItem connectionItem) {
        return repositoryValueUtils.getRepositoryAliasName(connectionItem);
    }

    public void switchToCurContextsView() {
        Contexts.switchToCurContextsView();
    }

    public void saveJobBeforeRun(IProcess activeProcess) {
        new SaveJobBeforeRunAction(activeProcess).run();
    }

    // ends.

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getCurrentProcess()
     */
    public IProcess getCurrentProcess() {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (!(editor instanceof MultiPageTalendEditor)) {
            return null;
        }
        IProcess process = ((MultiPageTalendEditor) editor).getProcess();
        return process;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#refreshDesignerPalette()
     */
    public void refreshDesignerPalette() {

        for (IEditorPart editor : ProcessorUtilities.getOpenedEditors()) {
            ((TalendEditor) editor).updatePaletteContent();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getActiveProcessTracker()
     */
    public IPartListener getActiveProcessTracker() {

        return new ActiveProcessTracker();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#getPreferenceStore(java.lang.String)
     */
    public String getPreferenceStore(String key) {
        return DesignerPlugin.getDefault().getPreferenceStore().getString(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IDesignerCoreService#createPalette(org.talend.core.model.components.IComponentsFactory)
     */
    public PaletteRoot createPalette(IComponentsFactory factory) {
        return TalendEditorPaletteFactory.createPalette(factory);
    }

    public IAction getCreateProcessAction(boolean isToolbar) {
        return new CreateProcess(isToolbar);
    }
}
