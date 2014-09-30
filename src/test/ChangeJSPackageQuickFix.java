package test;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ChangeJSPackageQuickFix implements LocalQuickFix {

    public static final String NAME = "ChangeJSPackageQuickFix";
    private final PsiFile jsFile;
    private final String oldValue;
    private final String newValue;

    public ChangeJSPackageQuickFix(PsiFile jsFile, String oldValue, String newValue) {
        this.jsFile = jsFile;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getFamilyName() {
        return NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

        jsFile.getTextRange().replace(oldValue, newValue);
    }
}
