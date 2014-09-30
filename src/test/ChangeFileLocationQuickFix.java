package test;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ChangeFileLocationQuickFix implements LocalQuickFix {
    public static final String NAME = "Change File location";
    private final PsiFile jsFile;
    private final String destination;

    public ChangeFileLocationQuickFix(PsiFile jsFile, String destination) {
        this.jsFile = jsFile;
        this.destination = destination;
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
        try {
            VirtualFile destinationDir = createDirInProject(project, destination);
            jsFile.getVirtualFile().move(this, destinationDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static VirtualFile createDirInProject(Project project, String destination) {
        VirtualFile destinationFile = project.getBaseDir().findFileByRelativePath(destination.replaceAll("\\.", "/"));
        if (destinationFile != null)
            return destinationFile;

        PsiDirectory destinationDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        return DirectoryUtil.createSubdirectories(destination, destinationDirectory, ".").getVirtualFile();
    }
}
