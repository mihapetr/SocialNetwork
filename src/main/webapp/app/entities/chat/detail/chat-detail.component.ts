import { Component, inject, input, signal, WritableSignal, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IChat } from '../chat.model';
import { AccountService } from '../../../core/auth/account.service';
import { FormsModule } from '@angular/forms';
import { Observable, Subscription, switchMap, timer } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IPost } from '../../post/post.model';
import { ChatService } from '../service/chat.service';
import { IMessage } from '../../message/message.model';

@Component({
  selector: 'jhi-chat-detail',
  templateUrl: './chat-detail.component.html',
  imports: [SharedModule, RouterModule, FormsModule],
})
export class ChatDetailComponent implements OnInit {
  chat = input<IChat | null>(null);
  account = inject(AccountService).trackCurrentAccount();
  localMessages: WritableSignal<IMessage[] | null | undefined> = signal(null);

  messageText = '';
  protected chatService = inject(ChatService);
  private pollingSubscription?: Subscription;

  ngOnInit(): void {
    // Initialize local writable signal with initial data from route resolver input signal
    this.localMessages.set(this.chat()?.chats);

    this.pollingSubscription = timer(0, 1000)
      .pipe(switchMap(() => this.chatService.find(this.chat()!.id)))
      .subscribe({
        next: response => {
          this.localMessages.set(response.body?.chats); // Process/update component data
        },
        error(error) {
          console.error('Polling error', error);
          // Optionally handle errors, stop polling, etc.
        },
      });
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
