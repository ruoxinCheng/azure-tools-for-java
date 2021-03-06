/**
 * Copyright (c) Microsoft Corporation
 * <p/>
 * All rights reserved.
 * <p/>
 * MIT License
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microsoft.intellij.serviceexplorer.azure.mobileservice;

import com.intellij.openapi.project.Project;
import com.microsoft.intellij.forms.CustomAPIForm;
import com.microsoft.tooling.msservices.helpers.Name;
import com.microsoft.tooling.msservices.helpers.azure.AzureCmdException;
import com.microsoft.tooling.msservices.helpers.azure.AzureManagerImpl;
import com.microsoft.tooling.msservices.model.ms.CustomAPI;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.mobileservice.CustomAPINode;
import com.microsoft.tooling.msservices.serviceexplorer.azure.mobileservice.MobileServiceNode;

import java.lang.reflect.InvocationTargetException;

@Name("Create API")
public class CreateAPIAction extends NodeActionListener {
    private MobileServiceNode mobileServiceNode;

    public CreateAPIAction(MobileServiceNode mobileServiceNode) {
        this.mobileServiceNode = mobileServiceNode;
    }

    @Override
    public void actionPerformed(NodeActionEvent e) {
        CustomAPIForm form = new CustomAPIForm((Project) mobileServiceNode.getProject());
        form.setServiceName(mobileServiceNode.getMobileService().getName());
        form.setSubscriptionId(mobileServiceNode.getMobileService().getSubcriptionId());

        form.setAfterSave(new Runnable() {
            @Override
            public void run() {
                // refresh the apis node
                mobileServiceNode.getCustomAPIsNode().removeAllChildNodes();
                try {
                    mobileServiceNode.loadServiceNode(
                            AzureManagerImpl.getManager().getAPIList(
                                    mobileServiceNode.getMobileService().getSubcriptionId(),
                                    mobileServiceNode.getMobileService().getName()),
                            "_apis",
                            MobileServiceNode.CUSTOM_APIS,
                            mobileServiceNode.getCustomAPIsNode(),
                            CustomAPINode.class,
                            CustomAPI.class);
                } catch (NoSuchMethodException e1) {
                    mobileServiceNode.handleError(e1);
                } catch (IllegalAccessException e1) {
                    mobileServiceNode.handleError(e1);
                } catch (InvocationTargetException e1) {
                    mobileServiceNode.handleError(e1);
                } catch (InstantiationException e1) {
                    mobileServiceNode.handleError(e1);
                } catch (AzureCmdException e1) {
                    mobileServiceNode.handleError(e1);
                }
            }
        });

        form.show();
    }
}
