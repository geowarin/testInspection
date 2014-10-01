package js;

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
    private VirtualFile root;
    private final PsiFile jsFile;
    private final String destinationPackage;

    public ChangeFileLocationQuickFix(VirtualFile root, PsiFile jsFile, String destinationPackage) {
        this.root = root;
        this.jsFile = jsFile;
        this.destinationPackage = destinationPackage;
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        try {
            VirtualFile destinationDir = createDirInProject(project, destinationPackage);
            PsiDirectory containingDirectory = jsFile.getContainingDirectory();
            jsFile.getVirtualFile().move(this, destinationDir);
            if (containingDirectory != null && isEmpty(containingDirectory)) {
                containingDirectory.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmpty(PsiDirectory containingDirectory) {
        return containingDirectory.getFiles().length == 0 && containingDirectory.getSubdirectories().length == 0;
    }

    private VirtualFile createDirInProject(Project project, String destination) {
        VirtualFile destinationFile = root.findFileByRelativePath(destination.replaceAll("\\.", "/"));
        if (destinationFile != null)
            return destinationFile;

        PsiDirectory destinationDirectory = PsiManager.getInstance(project).findDirectory(root);
        return DirectoryUtil.createSubdirectories(destination, destinationDirectory, ".").getVirtualFile();
    }
}
