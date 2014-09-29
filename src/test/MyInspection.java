package test;

import com.intellij.codeInspection.CustomSuppressableInspectionTool;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.SuppressIntentionAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.PsiPlainTextFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 29/09/2014
 * Time: 21:38
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class MyInspection extends LocalInspectionTool implements CustomSuppressableInspectionTool {


    public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement psiElement) {
        return new SuppressIntentionAction[0];
    }

    public boolean isSuppressedFor(PsiElement element) {
        PsiPlainTextFileImpl parent = PsiTreeUtil.getParentOfType(element, PsiPlainTextFileImpl.class, false);
//        element.getManager().getProject().getBaseDir()

        if ("JavaScript files".equals(parent.getFileType().getName())) {
            Project project = parent.getProject();
            VirtualFile jsFile = parent.getVirtualFile();
            System.out.println("Lol");
//            parent.getText()
//            parent.getVirtualFile().getExtension()
//            parent.getVirtualFile().getCanonicalPath()
            return false;
        }
        return true;
    }
}
