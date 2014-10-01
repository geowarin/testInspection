package js;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ChangeJSPackageQuickFix implements LocalQuickFix {
    public static final String NAME = "Change JS package";
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
        Document document = FileDocumentManager.getInstance().getDocument(jsFile.getVirtualFile());
        int start = document.getText().indexOf(oldValue);
        document.replaceString(start, start + oldValue.length(), newValue);
    }
}