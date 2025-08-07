import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'profile',
    data: { pageTitle: 'Profiles' },
    loadChildren: () => import('./profile/profile.routes'),
  },
  {
    path: 'post',
    data: { pageTitle: 'Posts' },
    loadChildren: () => import('./post/post.routes'),
  },
  {
    path: 'comment',
    data: { pageTitle: 'Comments' },
    loadChildren: () => import('./comment/comment.routes'),
  },
  {
    path: 'chat',
    data: { pageTitle: 'Chats' },
    loadChildren: () => import('./chat/chat.routes'),
  },
  {
    path: 'message',
    data: { pageTitle: 'Messages' },
    loadChildren: () => import('./message/message.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
