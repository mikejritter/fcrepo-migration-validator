package org.fcrepo.migration.validator.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.fcrepo.migration.validator.api.ResumeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shake
 */
public class ResumeManagerImpl implements ResumeManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResumeManagerImpl.class);

    private long count;
    private final boolean acceptAll;
    private final Path resumeFile;
    private final Set<String> processedPids;

    public ResumeManagerImpl(final Path resultsDir, final boolean acceptAll) {
        this.count = 0;
        this.acceptAll = acceptAll;
        this.resumeFile = resultsDir.resolve("resume.txt");
        this.processedPids = new ConcurrentSkipListSet<>();
        try {
            loadResumeFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadResumeFile() throws IOException {
        // touch the file so that we always have it
        if (!Files.exists(resumeFile)) {
            Files.createDirectories(resumeFile.getParent());
            Files.createFile(resumeFile);
        }

        try (var lines = Files.lines(resumeFile)) {
            lines.forEach(processedPids::add);
        }
    }

    public void updateResumeFile() {
        final String lineSeparator = System.lineSeparator();
        try (var writer = Files.newBufferedWriter(resumeFile, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String pid : processedPids) {
                writer.write(pid + lineSeparator);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void completed(final String pid) {
        processedPids.add(pid);
    }

    @Override
    public boolean accept(final String pid) {
        count++;
        final String logMsg = "PID: " + pid + ", accept? ";

        if (!acceptAll && processedPids.contains(pid)) {
            LOGGER.debug(logMsg + false);
            return false;
        }

        LOGGER.debug(logMsg + true);
        return true;
    }

    public long totalProcessed() {
        return count;
    }

}
