import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { PinService } from 'src/app/data/service/pin-service/pin.service';

@Component({
  selector: 'app-bucket-list',
  templateUrl: './bucket-list.component.html',
  styleUrls: ['./bucket-list.component.scss']
})
export class BucketListComponent implements OnInit {

  bucketList: any[] = [];

  constructor(private pinService: PinService,
    private toast: ToastrService,
    private spinner: NgxSpinnerService,) { }

  ngOnInit(): void {
    // get bucket list from pinService
    this.spinner.show();
    this.pinService.getBucketList()
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          this.bucketList = data.payload;
        },
        error: (err) => {
          this.toast.error(err.error?.message);
        }
      });
  }

  deleteFromBucketList(pinId: number){
    this.spinner.show();
    this.pinService.deleteFromBucketList(pinId)
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          this.toast.success(data.message);
          // find entry in bucket list with pinId and remove it
          const index = this.bucketList.findIndex(entity => entity?.pin.pinId === pinId);
          if(index !== -1){
            this.bucketList.splice(index, 1);
          }
        },
        error: (err) => {
          this.toast.error(err.error?.message);
        }
      });
  }

}
