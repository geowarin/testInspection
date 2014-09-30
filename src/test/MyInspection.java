package test;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void inspectionStarted(LocalInspectionToolSession session, boolean isOnTheFly) {
        System.out.println("inspection started");
        super.inspectionStarted(session, isOnTheFly);
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        System.out.println("checkfile " + file);
        ProblemDescriptor problem = null;

        if ("JavaScript files".equals(file.getFileType().getName())) {
            Project project = file.getProject();
            VirtualFile jsFile = file.getVirtualFile();

            Pattern pattern = Pattern.compile("definePackage\\(\"(.*)\"\\s*,.*", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(file.getText());
            if (matcher.matches()) {

                String projectPath = project.getBaseDir().getPath();
                String directoryPackage = jsFile.getParent().getPath().substring(projectPath.length() + 1).replaceAll("/", ".");
                String jsPackage = matcher.group(1);

                if (!directoryPackage.equals(jsPackage)) {

                    LocalQuickFix[] fixes = getFixes(file, directoryPackage, jsPackage);
                    problem = manager.createProblemDescriptor(file, "package " + jsPackage + " does not match " + directoryPackage, false, fixes, ProblemHighlightType.WEAK_WARNING);
                }
            }
        }
        return problem != null ? new ProblemDescriptor[]{problem} : null;
    }

    private LocalQuickFix[] getFixes(PsiFile jsFile, String directoryPackage, String jsPackage) {
        ChangeJSPackageQuickFix changeJSPackageQuickFix = new ChangeJSPackageQuickFix(jsFile, jsPackage, directoryPackage);

        return new LocalQuickFix[]{changeJSPackageQuickFix};
    }
}
