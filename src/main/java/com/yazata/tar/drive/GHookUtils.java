package com.yazata.tar.drive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GHookUtils {

	public static String getFolderId(String parentId, String name) throws IOException {
		String parentFolderId = "";
		if (name != "") {
			List<File> parentFolderMetadata = getGoogleSubFolderByName(parentId, null, name, "folder");
			if (parentFolderMetadata.size() > 0) {
				parentFolderId = (parentFolderMetadata.get(0)).getId();
			}
		}
		return parentFolderId;
	}

	public static final List<File> getGoogleSubFolderByName(String googleFolderIdParent, String googleFolderId,
			String subFolderName, String mimeType) throws IOException {

		Drive driveService = GoogleDriveUtils.getDriveService();

		String pageToken = null;
		List<File> list = new ArrayList<File>();
		String query = generateQuery(googleFolderIdParent, googleFolderId, subFolderName, mimeType);

		do {
			FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
					.setFields("nextPageToken, files(id, name, createdTime)")//
					.setPageToken(pageToken).execute();
			for (File file : result.getFiles()) {
				list.add(file);
			}
			pageToken = result.getNextPageToken();
		} while (pageToken != null);

		return list;
	}

	public static String generateQuery(String parentId, String id, String name, String mimeType) {
		String query = "";
		String parent = parentId != null && parentId != "" ? parentId : "root";
		query += "'" + parent + "' in parents";
		if (id != null && id != "") {
			query += " and  id = '" + id + "' ";

		} else if (name != null && name != "") {
			query += " and  name = '" + name + "' ";
		}
		if (mimeType != null && mimeType != "") {
			if (mimeType == "folder")
				query += " and mimeType = 'application/vnd.google-apps.folder' ";
		}
		return query;
	}
}
