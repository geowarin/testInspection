package test;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 29/09/2014
 * Time: 21:38
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class MyInspection extends LocalInspectionTool {

    public String getShortName() {
        return "myInspection";
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemDescriptor problem = null;

        if ("JavaScript files".equals(file.getFileType().getName())) {
            Project project = file.getProject();
            VirtualFile jsFile = file.getVirtualFile();

            Pattern pattern = Pattern.compile(".*definePackage\\(\"(.*)\"\\s*,.*", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(file.getText());
            if (matcher.matches()) {

                VirtualFile root = removeSpecial(getRoot(project, jsFile));
                String directoryPackage = getDirectoryPackage(root, jsFile);
                String jsPackage = matcher.group(1);

                if (!jsPackage.equals(directoryPackage)) {

                    LocalQuickFix[] fixes = getFixes(file, root, directoryPackage, jsPackage);
                    problem = manager.createProblemDescriptor(file, "package " + jsPackage + " does not match " + directoryPackage, false, fixes, ProblemHighlightType.WEAK_WARNING);
                }
            }
        }
        return problem != null ? new ProblemDescriptor[]{problem} : null;
    }

    private VirtualFile removeSpecial(VirtualFile root) {
        return getOne(root, "src/main/webapp/js", "src/main/webapp");
    }

    private VirtualFile getOne(VirtualFile root, String... paths) {
        for (String path : paths) {
            VirtualFile dir = root.findFileByRelativePath(path);
            if (dir != null) {
                return dir;
            }
        }
        return root;
    }

    private VirtualFile getRoot(Project project, VirtualFile jsFile) {
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile moduleSourceRoot = fileIndex.getSourceRootForFile(jsFile);
        if (moduleSourceRoot != null) {
            return moduleSourceRoot;
        }
        return fileIndex.getContentRootForFile(jsFile);
    }

    private String getDirectoryPackage(VirtualFile root, VirtualFile jsFile) {
        String projectPath = root.getPath();
        String parentPath = jsFile.getParent().getPath();
        if (projectPath.length() + 1 > parentPath.length())
            return null;
        return parentPath.substring(projectPath.length() + 1).replaceAll("/", ".");
    }

    private LocalQuickFix[] getFixes(PsiFile jsFile, VirtualFile root, String directoryPackage, String jsPackage) {
        List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();

        if (directoryPackage != null)
            fixes.add(new ChangeJSPackageQuickFix(jsFile, jsPackage, directoryPackage));
        fixes.add(new ChangeFileLocationQuickFix(root, jsFile, jsPackage));

        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }
}
