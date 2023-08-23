package com.yazata.tar.quartz;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.drive.model.File;
import com.yazata.tar.drive.GHookAdapter;
import com.yazata.tar.util.DateTimeUtils;

import lombok.extern.java.Log;

@Component
@Log
public class FileAccumulationTask {

	private static int fileCount = 0;

	@Scheduled(cron = "${interval}")
	public void execute() {
		Date startDate = new Date(System.currentTimeMillis());
		String rootFolderId = GHookAdapter.getRootFolderId();
		List<File> userFolderIds = GHookAdapter.getFoldersOrFiles(rootFolderId, "folder");
		for (File file : userFolderIds) {
			System.out.println("Processing user: " + file.getName());
			fileCount = 0;
			try {
				GHookAdapter.initiateFileMove(file.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Totel files moved for processing: " + fileCount);
			System.out.println("________________________________________________________________\n");
		}
		log.info(DateTimeUtils.printDifference(startDate));
	}
}
