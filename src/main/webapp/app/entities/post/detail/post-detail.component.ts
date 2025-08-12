import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IPost } from '../post.model';
import { PostService } from '../service/post.service';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormsModule],
})
export class PostDetailComponent {
  post = input<IPost | null>(null);

  commentText = '';
  protected dataUtils = inject(DataUtils);
  protected postService = inject(PostService);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  postComment(): void {
    this.subscribeToCommentResponse(this.postService.comment(this.post()!.id, this.commentText));
  }

  formatDateTime(isoString: string): string {
    const date = new Date(isoString);

    // Format day
    const day = date.getDate();

    // Format month as short string (e.g., Aug)
    const month = date.toLocaleString('en-US', { month: 'short' });

    // Format year
    const year = date.getFullYear();

    // Format time as HH:mm:ss with zero-padding
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    const pad = (n: number) => n.toString().padStart(2, '0');
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());

    return `${day} ${month} ${year} ${hours}:${minutes}`;
  }

  protected subscribeToCommentResponse(result: Observable<HttpResponse<IPost>>): void {
    result.subscribe({
      next: res => this.onSaveSuccess(res.body!),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(updatedPost: IPost): void {
    this.commentText = ''; // clear input
    this.post.bind(updatedPost); // update bound post signal
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
