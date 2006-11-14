// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.sqlbuilder.dbstructure.nodes;

import net.sourceforge.sqlexplorer.SQLAlias;

import org.eclipse.swt.graphics.Image;
import org.talend.sqlbuilder.util.ImageUtil;
import org.talend.sqlbuilder.util.TextUtil;

/**
 * DOC dev  class global comment. Detailled comment
 * <br/>
 *
 * $Id: AbstractFolderNode.java,v 1.3 2006/11/01 05:40:59 peiqin.hou Exp $
 *
 */
public abstract class AbstractFolderNode extends AbstractNode {

    private String[] pfilterExpressions;


    public AbstractFolderNode() {

        pimageKey = "Images.closedFolder";
        pexpandedImageKey = "Images.OpenFolder";
    }


    /**
     * Override this method to change the image that is displayed for this node
     * in the database structure outline.
     * @return Image
     */
    public Image getImage() {

        if (pimageKey == null) {
            return pimage;
        }
        return ImageUtil.getImage(pimageKey);
    }

    /**
     * @return Name.
     */
    public abstract String getName();

    /**
     * @return UniqueIdentifier
     */
    public final String getUniqueIdentifier() {

        return getParent().getName() + '.' + getType();
    }


    /**
     * Checks if a node name should be filtered.
     * 
     * @param name to check for filtering
     * @return true if the name should be filtered
     */
    protected boolean isExcludedByFilter(String name) {

        if (pfilterExpressions == null) {
            String filter = ((SQLAlias) getSession().getAlias()).getNameFilterExpression();
            if (filter != null) {
                pfilterExpressions = filter.split(",");
            }
        }
        if (pfilterExpressions == null || pfilterExpressions.length == 0) {
            // no active filter
            return false;
        }

        for (int i = 0; i < pfilterExpressions.length; i++) {

            String regex = pfilterExpressions[i].trim();
            regex = TextUtil.replaceChar(regex, '?', ".");
            regex = TextUtil.replaceChar(regex, '*', ".*");

            if (regex.length() != 0 && name.matches(regex)) {
                // we have a match, exclude node..
                return true;
            }
        }

        // no match found
        return false;

    }


    public abstract void loadChildren();
}
