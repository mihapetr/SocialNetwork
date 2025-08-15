import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IChat } from '../chat.model';
import { AccountService } from '../../../core/auth/account.service';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IPost } from '../../post/post.model';
import { ChatService } from '../service/chat.service';

@Component({
  selector: 'jhi-chat-detail',
  templateUrl: './chat-detail.component.html',
  imports: [SharedModule, RouterModule, FormsModule],
})
export class ChatDetailComponent {
  chat = input<IChat | null>(null);
  account = inject(AccountService).trackCurrentAccount();

  messageText = '';
  protected chatService = inject(ChatService);

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

  previousState(): void {
    window.history.back();
  }

  sendMessage(id: number): void {
    this.subscribeToMessageResponse(this.chatService.message(id, this.messageText));
  }

  protected subscribeToMessageResponse(result: Observable<HttpResponse<IPost>>): void {
    result.subscribe({
      next: res => this.onSaveSuccess(res.body!),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(updatedChat: IChat): void {
    this.messageText = ''; // clear input
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
