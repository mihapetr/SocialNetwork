import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IChat } from '../chat.model';
import { ChatService } from '../service/chat.service';
import { ChatFormService } from './chat-form.service';

import { ChatUpdateComponent } from './chat-update.component';

describe('Chat Management Update Component', () => {
  let comp: ChatUpdateComponent;
  let fixture: ComponentFixture<ChatUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let chatFormService: ChatFormService;
  let chatService: ChatService;
  let userService: UserService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ChatUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ChatUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChatUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    chatFormService = TestBed.inject(ChatFormService);
    chatService = TestBed.inject(ChatService);
    userService = TestBed.inject(UserService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const chat: IChat = { id: 26569 };
      const user: IUser = { id: 3944 };
      chat.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ chat });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profile query and add missing value', () => {
      const chat: IChat = { id: 26569 };
      const profiles: IProfile[] = [{ id: 32255 }];
      chat.profiles = profiles;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [...profiles];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ chat });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(expect.objectContaining),
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const chat: IChat = { id: 26569 };
      const user: IUser = { id: 3944 };
      chat.user = user;
      const profile: IProfile = { id: 32255 };
      chat.profiles = [profile];

      activatedRoute.data = of({ chat });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.profilesSharedCollection).toContainEqual(profile);
      expect(comp.chat).toEqual(chat);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChat>>();
      const chat = { id: 7587 };
      jest.spyOn(chatFormService, 'getChat').mockReturnValue(chat);
      jest.spyOn(chatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: chat }));
      saveSubject.complete();

      // THEN
      expect(chatFormService.getChat).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(chatService.update).toHaveBeenCalledWith(expect.objectContaining(chat));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChat>>();
      const chat = { id: 7587 };
      jest.spyOn(chatFormService, 'getChat').mockReturnValue({ id: null });
      jest.spyOn(chatService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chat: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: chat }));
      saveSubject.complete();

      // THEN
      expect(chatFormService.getChat).toHaveBeenCalled();
      expect(chatService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChat>>();
      const chat = { id: 7587 };
      jest.spyOn(chatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(chatService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfile', () => {
      it('Should forward to profileService', () => {
        const entity = { id: 32255 };
        const entity2 = { id: 13324 };
        jest.spyOn(profileService, 'compareProfile');
        comp.compareProfile(entity, entity2);
        expect(profileService.compareProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
