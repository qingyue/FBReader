/*
 * Copyright (C) 2007-2012 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.library;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.filesystem.ZLPhysicalFile;
import org.geometerplus.zlibrary.core.image.ZLFileImage;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.core.image.ZLLoadableImage;
import org.geometerplus.zlibrary.core.util.ZLMiscUtil;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

import org.geometerplus.fbreader.formats.*;
import org.geometerplus.fbreader.bookmodel.BookReadingException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.data.util.RefValue;

public class Book {
    private static final String TAG = "Book";
    
	public static Book getById(long bookId) {
		final Book book = BooksDatabase.Instance().loadBook(bookId);
		if (book == null) {
			return null;
		}
		book.loadLists();

		final ZLFile bookFile = book.File;
		final ZLPhysicalFile physicalFile = bookFile.getPhysicalFile();
		if (physicalFile == null) {
			return book;
		}
		if (!physicalFile.exists()) {
			return null;
		}

		FileInfoSet fileInfos = new FileInfoSet(physicalFile);
		if (fileInfos.check(physicalFile, physicalFile != bookFile)) {
			return book;
		}
		fileInfos.save();

		return book.readMetaInfo() ? book : null;
	}

	public static Book getByFile(ZLFile bookFile) {
		if (bookFile == null) {
			return null;
		}

		final ZLPhysicalFile physicalFile = bookFile.getPhysicalFile();
		if (physicalFile != null && !physicalFile.exists()) {
			return null;
		}

		final FileInfoSet fileInfos = new FileInfoSet(bookFile);

		Book book = BooksDatabase.Instance().loadBookByFile(fileInfos.getId(bookFile), bookFile);
		if (book != null) {
			book.loadLists();
		}

		if (book != null && fileInfos.check(physicalFile, physicalFile != bookFile)) {
			return book;
		}
		fileInfos.save();

		if (book == null) {
			book = new Book(bookFile);
		}
		if (book.readMetaInfo()) {
			book.save();
			return book;
		}
		return null;
	}

	public final ZLFile File;

	private long myId;

	private String myEncoding;
	private String myLanguage;
	private String myTitle;
	private List<Author> myAuthors;
	private List<Tag> myTags;
	private SeriesInfo mySeriesInfo;

	private boolean myIsSaved;

	private static final WeakReference<ZLImage> NULL_IMAGE = new WeakReference<ZLImage>(null);
	private WeakReference<ZLImage> myCover;

	Book(long id, ZLFile file, String title, String encoding, String language) {
		myId = id;
		File = file;
		myTitle = title;
		myEncoding = encoding;
		myLanguage = language;
		myIsSaved = true;
	}

	Book(ZLFile file) {
		myId = -1;
		File = file;
	}

	public void reloadInfoFromFile() {
		if (readMetaInfo()) {
			save();
		}
	}

	public void reloadInfoFromDatabase() {
		final BooksDatabase database = BooksDatabase.Instance();
		database.reloadBook(this);
		myAuthors = database.loadAuthors(myId);
		myTags = database.loadTags(myId);
		mySeriesInfo = database.loadSeriesInfo(myId);
		myIsSaved = true;
	}

	boolean readMetaInfo() {
		myEncoding = null;
		myLanguage = null;
		myTitle = null;
		myAuthors = null;
		myTags = null;
		mySeriesInfo = null;

		myIsSaved = false;

		final FormatPlugin plugin = PluginCollection.Instance().getPlugin(File);
		if (plugin == null) {
			return false;
		}
		try {
			plugin.readMetaInfo(this);
		} catch (BookReadingException e) {
			return false;
		}
		if (myTitle == null || myTitle.length() == 0) {
			final String fileName = File.getShortName();
			final int index = fileName.lastIndexOf('.');
			setTitle(index > 0 ? fileName.substring(0, index) : fileName);
		}
		final String demoPathPrefix = Paths.BooksDirectoryOption().getValue() + java.io.File.separator + "Demos" + java.io.File.separator;
		if (File.getPath().startsWith(demoPathPrefix)) {
			final String demoTag = LibraryUtil.resource().getResource("demo").getValue();
			setTitle(getTitle() + " (" + demoTag + ")");
			addTag(demoTag);
		}
		return true;
	}

	private void loadLists() {
		final BooksDatabase database = BooksDatabase.Instance();
		myAuthors = database.loadAuthors(myId);
		myTags = database.loadTags(myId);
		mySeriesInfo = database.loadSeriesInfo(myId);
		myIsSaved = true;
	}

	public List<Author> authors() {
		return (myAuthors != null) ? Collections.unmodifiableList(myAuthors) : Collections.<Author>emptyList();
	}

	void addAuthorWithNoCheck(Author author) {
		if (myAuthors == null) {
			myAuthors = new ArrayList<Author>();
		}
		myAuthors.add(author);
	}

	private void addAuthor(Author author) {
		if (author == null) {
			return;
		}
		if (myAuthors == null) {
			myAuthors = new ArrayList<Author>();
			myAuthors.add(author);
			myIsSaved = false;
		} else if (!myAuthors.contains(author)) {
			myAuthors.add(author);
			myIsSaved = false;
		}
	}

	public void addAuthor(String name) {
		addAuthor(name, "");
	}

	public void addAuthor(String name, String sortKey) {
		String strippedName = name;
		strippedName.trim();
		if (strippedName.length() == 0) {
			return;
		}

		String strippedKey = sortKey;
		strippedKey.trim();
		if (strippedKey.length() == 0) {
			int index = strippedName.lastIndexOf(' ');
			if (index == -1) {
				strippedKey = strippedName;
			} else {
				strippedKey = strippedName.substring(index + 1);
				while ((index >= 0) && (strippedName.charAt(index) == ' ')) {
					--index;
				}
				strippedName = strippedName.substring(0, index + 1) + ' ' + strippedKey;
			}
		}

		addAuthor(new Author(strippedName, strippedKey));
	}

	public long getId() {
		return myId;
	}

	public String getTitle() {
		return myTitle;
	}

	public void setTitle(String title) {
		if (!ZLMiscUtil.equals(myTitle, title)) {
			myTitle = title;
			myIsSaved = false;
		}
	}

	public SeriesInfo getSeriesInfo() {
		return mySeriesInfo;
	}

	void setSeriesInfoWithNoCheck(String name, float index) {
		mySeriesInfo = new SeriesInfo(name, index);
	}

	public void setSeriesInfo(String name, float index) {
		if (mySeriesInfo == null) {
			if (name != null) {
				mySeriesInfo = new SeriesInfo(name, index);
				myIsSaved = false;
			}
		} else if (name == null) {
			mySeriesInfo = null;
			myIsSaved = false;
		} else if (!name.equals(mySeriesInfo.Name) || mySeriesInfo.Index != index) {
			mySeriesInfo = new SeriesInfo(name, index);
			myIsSaved = false;
		}
	}

	public String getLanguage() {
		return myLanguage;
	}

	public void setLanguage(String language) {
		if (!ZLMiscUtil.equals(myLanguage, language)) {
			myLanguage = language;
			myIsSaved = false;
		}
	}

	public String getEncoding() {
		return myEncoding;
	}

	public void setEncoding(String encoding) {
		if (!ZLMiscUtil.equals(myEncoding, encoding)) {
			myEncoding = encoding;
			myIsSaved = false;
		}
	}

	public List<Tag> tags() {
		return (myTags != null) ? Collections.unmodifiableList(myTags) : Collections.<Tag>emptyList();
	}

	void addTagWithNoCheck(Tag tag) {
		if (myTags == null) {
			myTags = new ArrayList<Tag>();
		}
		myTags.add(tag);
	}

	public void addTag(Tag tag) {
		if (tag != null) {
			if (myTags == null) {
				myTags = new ArrayList<Tag>();
			}
			if (!myTags.contains(tag)) {
				myTags.add(tag);
				myIsSaved = false;
			}
		}
	}

	public void addTag(String tagName) {
		addTag(Tag.getTag(null, tagName));
	}

	boolean matches(String pattern) {
		if (myTitle != null && ZLMiscUtil.matchesIgnoreCase(myTitle, pattern)) {
			return true;
		}
		if (mySeriesInfo != null && ZLMiscUtil.matchesIgnoreCase(mySeriesInfo.Name, pattern)) {
			return true;
		}
		if (myAuthors != null) {
			for (Author author : myAuthors) {
				if (ZLMiscUtil.matchesIgnoreCase(author.DisplayName, pattern)) {
					return true;
				}
			}
		}
		if (myTags != null) {
			for (Tag tag : myTags) {
				if (ZLMiscUtil.matchesIgnoreCase(tag.Name, pattern)) {
					return true;
				}
			}
		}
		if (ZLMiscUtil.matchesIgnoreCase(File.getLongName(), pattern)) {
			return true;
		}
		return false;
	}

	public boolean save() {
		if (myIsSaved) {
			return false;
		}
		final BooksDatabase database = BooksDatabase.Instance();
		database.executeAsATransaction(new Runnable() {
			public void run() {
				if (myId >= 0) {
					final FileInfoSet fileInfos = new FileInfoSet(File);
					database.updateBookInfo(myId, fileInfos.getId(File), myEncoding, myLanguage, myTitle);
				} else {
					myId = database.insertBookInfo(File, myEncoding, myLanguage, myTitle);
					storeAllVisitedHyperinks();
				}

				long index = 0;
				database.deleteAllBookAuthors(myId);
				for (Author author : authors()) {
					database.saveBookAuthorInfo(myId, index++, author);
				}
				database.deleteAllBookTags(myId);
				for (Tag tag : tags()) {
					database.saveBookTagInfo(myId, tag);
				}
				database.saveBookSeriesInfo(myId, mySeriesInfo);
			}
		});
		
		try {
		    OnyxMetadata data = new OnyxMetadata();
		    java.io.File file = new java.io.File(File.getPath());
		    
            long time_point = System.currentTimeMillis();
            String md5 = FileUtil.computeMD5(file);
            long time_md5 = System.currentTimeMillis() - time_point;
            Log.d(TAG, "times md5: " + time_md5);

            data.setMD5(md5);
            data.setName(file.getName());
            data.setLocation(file.getAbsolutePath());
            data.setNativeAbsolutePath(file.getAbsolutePath());
            data.setSize(file.length());
            data.setlastModified(file.lastModified());
            
            Context ctx = ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getActivity();
            if (OnyxCmsCenter.getMetadata(ctx, data)) {
                data.setTitle(myTitle);
                ArrayList<String> authors = new ArrayList<String>();
                if (myAuthors != null) {
                    for (Author a : myAuthors) {
                        authors.add(a.DisplayName);
                    }
                }
                data.setAuthors(authors);
                data.setLanguage(myLanguage);
                data.setEncoding(myEncoding);
                ArrayList<String> tags = new ArrayList<String>();
                if (myTags != null) {
                    for (Tag t : myTags) {
                        tags.add(t.Name);
                    }
                    data.setTags(tags);
                }
                
                OnyxCmsCenter.updateMetadata(ctx, data);
            }
            else {
                data.setTitle(myTitle);
                ArrayList<String> authors = new ArrayList<String>();
                if (myAuthors != null) {
                    for (Author a : myAuthors) {
                        authors.add(a.DisplayName);
                    }
                }
                data.setAuthors(authors);
                data.setLanguage(myLanguage);
                data.setEncoding(myEncoding);
                ArrayList<String> tags = new ArrayList<String>();
                if (myTags != null) {
                    for (Tag t : myTags) {
                        tags.add(t.Name);
                    }
                    data.setTags(tags);
                }
                
                OnyxCmsCenter.insertMetadata(ctx, data);
            }
            
            Log.d(TAG, "check cover");
            ZLImage image = this.getCover();
            if (image != null) {
                Log.d(TAG, "cover is not null");
                RefValue<Bitmap> result = new RefValue<Bitmap>();
                if (!OnyxCmsCenter.getThumbnail(ctx, data, result)) {
                    if (image instanceof ZLLoadableImage) {
                        final ZLLoadableImage loadableImage = (ZLLoadableImage)image;
                        if (!loadableImage.isSynchronized()) {
                            loadableImage.synchronize();
                        }
                    }
                    
                    final ZLAndroidImageData image_data =
                            ((ZLAndroidImageManager)ZLAndroidImageManager.Instance()).getImageData(image);
                    if (image_data != null) {
                        Log.d(TAG, "image data not null, begin insert thumbnail");
                        final DisplayMetrics metrics = new DisplayMetrics();
                        Activity a = ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getActivity();
                        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        final int maxHeight = metrics.heightPixels * 2 / 3;
                        final int maxWidth = maxHeight * 2 / 3;
                        final Bitmap cover = image_data.getBitmap(2 * maxWidth, 2 * maxHeight);
                        if (cover != null) {
                            Log.d(TAG, "cover bitmap is not null"); 
                            if (!OnyxCmsCenter.insertThumbnail(ctx, data, cover)) {
                                Log.d(TAG, "insert thumbnail failed");
                            }
                            else {
                                Log.d(TAG, "insert thumbnail successfully");
                            }
                        }
                        else {
                            Log.d(TAG, "cover bitmap is null"); 
                        }
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.w(TAG, "exception caught: ", e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "exception caught: ", e);
            return false;
        } 

		myIsSaved = true;
		return true;
	}

	public ZLTextPosition getStoredPosition() {
		return BooksDatabase.Instance().getStoredPosition(myId);
	}

	public void storePosition(ZLTextPosition position) {
		if (myId != -1) {
			BooksDatabase.Instance().storePosition(myId, position);
		}
	}

	private Set<String> myVisitedHyperlinks;
	private void initHyperlinkSet() {
		if (myVisitedHyperlinks == null) {
			myVisitedHyperlinks = new TreeSet<String>();
			if (myId != -1) {
				myVisitedHyperlinks.addAll(BooksDatabase.Instance().loadVisitedHyperlinks(myId));
			}
		}
	}

	public boolean isHyperlinkVisited(String linkId) {
		initHyperlinkSet();
		return myVisitedHyperlinks.contains(linkId);
	}

	public void markHyperlinkAsVisited(String linkId) {
		initHyperlinkSet();
		if (!myVisitedHyperlinks.contains(linkId)) {
			myVisitedHyperlinks.add(linkId);
			if (myId != -1) {
				BooksDatabase.Instance().addVisitedHyperlink(myId, linkId);
			}
		}
	}

	private void storeAllVisitedHyperinks() {
		if (myId != -1 && myVisitedHyperlinks != null) {
			for (String linkId : myVisitedHyperlinks) {
				BooksDatabase.Instance().addVisitedHyperlink(myId, linkId);
			}
		}
	}

	public void insertIntoBookList() {
		if (myId != -1) {
			BooksDatabase.Instance().insertIntoBookList(myId);
		}
	}

	public String getContentHashCode() {
		InputStream stream = null;

		try {
			final MessageDigest hash = MessageDigest.getInstance("SHA-256");
			stream = File.getInputStream();

			final byte[] buffer = new byte[2048];
			while (true) {
				final int nread = stream.read(buffer);
				if (nread == -1) {
					break;
				}
				hash.update(buffer, 0, nread);
			}

			final Formatter f = new Formatter();
			for (byte b : hash.digest()) {
				f.format("%02X", b & 0xFF);
			}
			return f.toString();
		} catch (IOException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	synchronized ZLImage getCover() {
		if (myCover == NULL_IMAGE) {
			return null;
		} else if (myCover != null) {
			final ZLImage image = myCover.get();
			if (image != null) {
				return image;
			}
		}
		ZLImage image = null;
		final FormatPlugin plugin = PluginCollection.Instance().getPlugin(File);
		if (plugin != null) {
			image = plugin.readCover(File);
		}
		myCover = image != null ? new WeakReference<ZLImage>(image) : NULL_IMAGE;
		return image;
	}

	@Override
	public int hashCode() {
		return (int)myId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Book)) {
			return false;
		}
		return File.equals(((Book)o).File);
	}
}
