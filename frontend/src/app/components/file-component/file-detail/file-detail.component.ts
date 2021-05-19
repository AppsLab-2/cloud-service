import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { File } from '../../../models/file';
import { FileService } from '../../../services/file.service';
import * as fileSaver from 'file-saver';
import {MatDialog} from '@angular/material/dialog';
import {EditFileDialogComponent} from '../edit-file-dialog/edit-file-dialog.component';
import { getFileUrl } from '../../../utils/utils';

@Component({
  selector: 'app-file-detail',
  templateUrl: './file-detail.component.html',
  styleUrls: ['./file-detail.component.css']
})
export class FileDetailComponent implements OnInit {

  @Input() file: File;
  @Output() closeEvent = new EventEmitter<File>();
  constructor(private fileService: FileService, private dialog: MatDialog) { }

  get fileUrl(): string {
    return getFileUrl(this.file);
  }
  ngOnInit(): void { }
  openDialog(): void {
    const dialogRef = this.dialog.open(EditFileDialogComponent, { data: this.file });
    dialogRef.afterClosed().subscribe(newFile => {
      this.editFile(newFile);
    });
  }

  closeFile(): void {
    this.closeEvent.emit(null);
  }
  downloadFile(): void {
    this.fileService.downloadFile(this.file).subscribe(blob => {
      fileSaver.saveAs(blob, this.file.fileName);
    });
  }
  editFile(newFile: File): void {
    this.fileService.updateFile(newFile).subscribe();
  }
}
