import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { File as FileModel } from '../models/file';
import { HttpClient } from '@angular/common/http';
import {Folder} from '../models/folder';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private http: HttpClient) {
  }
  fileUrl = `${environment.apiUrl}/file`;

  folderUrl = `${environment.apiUrl}/folder`;

  getUserFiles(): Observable<FileModel[]> {
    return this.http.get<FileModel[]>(this.fileUrl);
  }
  getUserFile(id: number): Observable<FileModel> {
    return this.http.get<FileModel>(`${this.fileUrl}/${id}`);
  }
  getPublicFiles(): Observable<FileModel[]> {
    return this.http.get<FileModel[]>(`${this.fileUrl}/allFiles`);
  }

  uploadFile(file: File): Observable<FileModel> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileModel>(this.fileUrl, formData);
  }

  uploadLinkFile(file: FileModel): Observable<FileModel> {
    return this.http.post<FileModel>(`${this.fileUrl}/uploadLink`, file);
  }

  updateFile(file: FileModel): Observable<FileModel> {
    return this.http.put<FileModel>(`${this.fileUrl}/edit`, file);
  }

  downloadFile(file: FileModel): Observable<Blob> {
    return this.http.get(`${this.fileUrl}/${file.id}`, { responseType: 'blob' });
  }
  searchUserFiles(keyword: string): Observable<FileModel[]> {
    return this.http.get<FileModel[]>(`${this.fileUrl}/search/?keyword=${keyword}`);
  }
  searchPublicFiles(keyword: string): Observable<FileModel[]> {
    return this.http.get<FileModel[]>(`${this.fileUrl}/allFiles/search/?keyword=${keyword}`);
  }
  // folders

  getFolder(): Observable<Folder[]> {
    return this.http.get<Folder[]>(this.folderUrl);
  }
  createFolder(folder: Folder): Observable<Folder[]> {
    return this.http.post<Folder[]>(this.folderUrl, folder);
  }
  updateFolder(folder: Folder): Observable<Folder[]> {
    return this.http.put<Folder[]>(this.folderUrl, folder);
  }
  searchFolders(keyword: string): Observable<Folder[]> {
    return this.http.get<Folder[]>(`${this.folderUrl}/search/?keyword=${keyword}`);
  }
}
