import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlat } from '../plat.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-plat-detail',
  templateUrl: './plat-detail.component.html',
})
export class PlatDetailComponent implements OnInit {
  plat: IPlat | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plat }) => {
      this.plat = plat;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
