import * as moment from 'moment';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AuditService } from '../service/audit.service';
import { MatTableDataSource, MatSort, PageEvent, MatDatepickerInputEvent } from '@angular/material';

@Component({
  selector: 'app-view-audits',
  templateUrl: './view-audits.component.html',
  styleUrls: ['./view-audits.component.css']
})
export class ViewAuditsComponent implements OnInit {
  audits: any;
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'timestamp', 'action'];
  dataSource = new MatTableDataSource([]);
  error = false;
  loaded = true;
  selectedAudit: any;
  numberOfElements = 10;
  totalPages = 0;
  pageNumber = 0;
  totalElements = 0;
  size = 0;
  fromDate: Date = new Date('2019-01-01');
  toDate: Date = new Date();
  includeClosed = true;
  includeIncomplete = true;

  pageEvent: PageEvent;

  @ViewChild(MatSort) sort: MatSort;

  constructor(private auditService: AuditService) { }

  async ngOnInit() {
    this.getAudits();
  }

  async handlePage(pageEvent: PageEvent) {
    this.pageNumber = pageEvent.pageIndex;
    this.numberOfElements = pageEvent.pageSize;
    this.getAudits();
  }

  async updateFromDate(event: MatDatepickerInputEvent<Date>) {
    this.fromDate = event.value;
    this.getAudits();
  }

  async updateToDate(event: MatDatepickerInputEvent<Date>) {
    this.toDate = event.value;
    this.getAudits();
  }

  formatDate(date: Date) {
    return moment(date).format();
  }

  formatDate2(date: any) {
    return moment(date).toLocaleString();
  }

  async getAudit(id) {
    this.selectedAudit = await this.auditService.getAudit(id);
  }

  async getAudits() {
    this.audits = await this.auditService.searchAudits(this.formatDate(this.fromDate), this.formatDate(this.toDate),
     this.pageNumber, this.numberOfElements, this.includeClosed, this.includeIncomplete);

    this.dataSource = new MatTableDataSource(this.audits.content);
    this.dataSource.sort = this.sort;

    // set pagination data
    this.numberOfElements = this.audits.numberOfElements;
    this.totalPages = this.audits.totalPages;
    this.totalElements = this.audits.totalElements;
    this.size = this.audits.size;
  }
}
