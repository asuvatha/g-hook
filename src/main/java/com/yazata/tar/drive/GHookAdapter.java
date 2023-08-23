package com.yazata.tar.drive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.yazata.tar.util.DateTimeUtils;

import lombok.extern.java.Log;

@Log
public class GHookAdapter {

	private static int fileCount = 0;

	public static String getRootFolderId() {
		String parentFolderName = "TAR";
		String parentFolderId = "";
		try {
			parentFolderId = GHookUtils.getFolderId(null, parentFolderName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parentFolderId;
	}

	public static List<File> getFoldersOrFiles(String rootFolderId, String mimeType) {
		List<File> userFolderIds = new ArrayList<File>();
		try {
			List<File> userFolders = GHookUtils.getGoogleSubFolderByName(rootFolderId, null, "", mimeType);
			for (File file : userFolders) {
				userFolderIds.add(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userFolderIds;
	}

	public static void initiateFileMove(String userFolderId) throws Exception {

		String srcFolderName = "source";
		String destFolderName = "processing";
		List<File> userFolders = getFoldersOrFiles(userFolderId, "folder");

		List<File> srcFileMetaDataList = userFolders.stream()
				.filter(item -> item.getName().equalsIgnoreCase(srcFolderName)).collect(Collectors.toList());

		List<File> destFileMetaDataList = userFolders.stream()
				.filter(item -> item.getName().equalsIgnoreCase(destFolderName)).collect(Collectors.toList());
		String srcFolderId = srcFileMetaDataList.get(0).getId();
		String destFolderId = destFileMetaDataList.get(0).getId();

		List<File> srcFolderItemList = getFoldersOrFiles(srcFolderId, null);
		if (srcFolderItemList.size() > 0) {
			for (File fileItem : srcFolderItemList) {
				System.out.println("Moving \n\t" + fileItem.getName() + " from source to processing...");
				moveFileToFolder(fileItem.getId(), destFolderId);
			}
		} else {
			System.out.println("No Files available to move");
		}

	}

	/**
	 * @param fileId   Id of file to be moved.
	 * @param folderId Id of folder where the fill will be moved.
	 * @return list of parent ids for the file.
	 */
	public static List<String> moveFileToFolder(String fileId, String folderId) throws IOException {

		Drive service = GoogleDriveUtils.getDriveService();

		// Retrieve the existing parents to remove
		File file = service.files().get(fileId).setFields("parents").execute();
		StringBuilder previousParents = new StringBuilder();
		for (String parent : file.getParents()) {
			previousParents.append(parent);
			previousParents.append(',');
		}
		try {
			// Move the file to the new folder
			file = service.files().update(fileId, null).setAddParents(folderId)
					.setRemoveParents(previousParents.toString()).setFields("id, parents").execute();
			System.out.println("\t******** File moved");
			fileCount++;
			return file.getParents();
		} catch (GoogleJsonResponseException e) {
			System.err.println("Unable to move file: " + e.getDetails());
			throw e;
		}
	}

	public static void listFiles(String userFolderId) {
		System.out.println("Files");
		List<File> subFolderIds = getFoldersOrFiles(userFolderId, "folder");
		for (File file2 : subFolderIds) {
			if (StringUtils.equalsIgnoreCase("source", file2.getName())) {
				List<File> files = getFoldersOrFiles(file2.getId(), null);
				for (File file3 : files) {
					System.out.println("\t" + file3.getName());
				}
			}
		}
	}

	public static void main(String[] args) {
		Date startDate = new Date(System.currentTimeMillis());
		String rootFolder = getRootFolderId();
		List<File> userFolderIds = getFoldersOrFiles(rootFolder, "folder");
		for (File file : userFolderIds) {
			System.out.println("Processing user: " + file.getName());
			fileCount = 0;
			try {
				initiateFileMove(file.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Totel files moved for processing: " + fileCount);
			System.out.println("________________________________________________________________\n");
		}
		log.info(DateTimeUtils.printDifference(startDate));
	}
}
