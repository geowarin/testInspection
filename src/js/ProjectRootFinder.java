package js;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Date: 01/10/2014
 * Time: 22:04
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class ProjectRootFinder {
    private Project project;
    private String[] roots = new String[0];

    public ProjectRootFinder(Project project) {
        this.project = project;
    }

    public void considerRoots(String... roots) {
        this.roots = roots;
    }

    public VirtualFile getSourceOrContentRoot(VirtualFile jsFile) {
        VirtualFile sourceDir = getProjectSourceOrContentRoot(jsFile);
        return findOne(sourceDir, roots);
    }

    public String getPathRelativeToRoot(VirtualFile root, VirtualFile jsFile) {
        String projectPath = root.getPath();
        String parentPath = jsFile.getParent().getPath();
        if (projectPath.length() + 1 > parentPath.length())
            return null;
        return parentPath.substring(projectPath.length() + 1).replaceAll("/", ".");
    }

    private VirtualFile findOne(VirtualFile root, String... paths) {
        for (String path : paths) {
            VirtualFile dir = root.findFileByRelativePath(path);
            if (dir != null) {
                return dir;
            }
        }
        return root;
    }

    private VirtualFile getProjectSourceOrContentRoot(VirtualFile jsFile) {
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile moduleSourceRoot = fileIndex.getSourceRootForFile(jsFile);
        if (moduleSourceRoot != null) {
            return moduleSourceRoot;
        }
        return fileIndex.getContentRootForFile(jsFile);
    }
}
