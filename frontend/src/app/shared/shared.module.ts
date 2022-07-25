import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

//prime-ng imports
import {PanelModule} from 'primeng/panel';
import {ImageModule} from 'primeng/image';
import {CardModule} from 'primeng/card';
import {DividerModule} from 'primeng/divider';
import { ButtonModule } from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import { InputTextModule } from 'primeng/inputtext';
import {AvatarModule} from 'primeng/avatar';
import {FileUploadModule} from 'primeng/fileupload';
import {InputNumberModule} from 'primeng/inputnumber';
import { TabViewModule } from 'primeng/tabview';
import {EditorModule} from 'primeng/editor';
import {FieldsetModule} from 'primeng/fieldset';
import { MenuModule } from 'primeng/menu';
import {TableModule} from 'primeng/table';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {CarouselModule} from 'primeng/carousel';
import {RatingModule} from 'primeng/rating';

//ngx-spinner import
import { NgxSpinnerModule } from "ngx-spinner";
import { MeterToKilometerPipe } from './pipes/meter-to-kilometer/meter-to-kilometer.pipe';

@NgModule({
  declarations: [
    MeterToKilometerPipe
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    PanelModule,
    ImageModule,
    CardModule,
    DividerModule,
    ButtonModule,
    DialogModule,
    MessagesModule,
    MessageModule,
    ReactiveFormsModule,
    InputTextModule,
    AvatarModule,
    FileUploadModule,
    NgxSpinnerModule,
    InputNumberModule,
    FormsModule,
    TabViewModule,
    EditorModule,
    FieldsetModule,
    MenuModule,
    TableModule,
    InputTextareaModule,
    CarouselModule,
    RatingModule,    
  ],
  exports: [
    HttpClientModule,
    PanelModule,
    ImageModule,
    CardModule,
    DividerModule,
    ButtonModule,
    DialogModule,
    ReactiveFormsModule,
    MessagesModule,
    MessageModule,
    InputTextModule,
    AvatarModule,
    FileUploadModule,
    NgxSpinnerModule,
    InputNumberModule,
    FormsModule,
    MeterToKilometerPipe,
    TabViewModule,
    EditorModule,
    FieldsetModule,
    MenuModule,
    TableModule,
    InputTextareaModule,
    CarouselModule,
    RatingModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: []
})
export class SharedModule { }
