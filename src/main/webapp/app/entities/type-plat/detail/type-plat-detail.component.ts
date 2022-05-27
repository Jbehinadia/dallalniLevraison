import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITypePlat } from '../type-plat.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-type-plat-detail',
  templateUrl: './type-plat-detail.component.html',
})
export class TypePlatDetailComponent implements OnInit {
  typePlat: ITypePlat | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typePlat }) => {
      this.typePlat = typePlat;
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
