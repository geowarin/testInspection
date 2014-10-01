package js;

import com.intellij.codeInspection.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.codeInspection.ProblemHighlightType.*;

/**
 * Date: 29/09/2014
 * Time: 21:38
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class JsPackageDoesNotMatchDirectoryInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public String getShortName() {
        return "JsPackageDoesNotMatchDirectoryInspection";
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!isJavascriptFile(file)) {
            return null;
        }

        VirtualFile jsFile = file.getVirtualFile();
        String jsPackage = getDefinedJsPackage(file);

        if (jsPackage == null) {
            return null;
        }

        ProjectRootFinder projectRootFinder = new ProjectRootFinder(file.getProject());
        projectRootFinder.considerRoots("src/main/webapp/js", "src/main/webapp");
        VirtualFile root = projectRootFinder.getSourceOrContentRoot(jsFile);
        String directoryPackage = projectRootFinder.getPathRelativeToRoot(root, jsFile);

        if (jsPackage.equals(directoryPackage)) {
            return null;
        }

        LocalQuickFix[] fixes = getFixes(jsFile, root, directoryPackage, jsPackage);
        String errorMessage = String.format("Package %s does not match %s", jsPackage, directoryPackage);
        ProblemDescriptor problem = manager.createProblemDescriptor(file, errorMessage, false, fixes, WEAK_WARNING);
        return new ProblemDescriptor[] {problem};
    }

    private String getDefinedJsPackage(PsiFile file) {
        Pattern pattern = Pattern.compile(".*definePackage\\(\"(.*)\"\\s*,.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(file.getText());
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    private static boolean isJavascriptFile(PsiFile file) {
        return "JavaScript files".equals(file.getFileType().getName());
    }

    private LocalQuickFix[] getFixes(VirtualFile jsFile, VirtualFile root, String directoryPackage, String jsPackage) {
        List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();

        if (directoryPackage != null)
            fixes.add(new ChangeJSPackageQuickFix(jsFile, jsPackage, directoryPackage));
        fixes.add(new ChangeFileLocationQuickFix(root, jsFile, jsPackage));

        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }
}
