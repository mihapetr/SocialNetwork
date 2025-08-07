import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IChat } from 'app/entities/chat/chat.model';
import { ChatService } from 'app/entities/chat/service/chat.service';
import { IProfile } from '../profile.model';
import { ProfileService } from '../service/profile.service';
import { ProfileFormService } from './profile-form.service';

import { ProfileUpdateComponent } from './profile-update.component';

describe('Profile Management Update Component', () => {
  let comp: ProfileUpdateComponent;
  let fixture: ComponentFixture<ProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let profileFormService: ProfileFormService;
  let profileService: ProfileService;
  let userService: UserService;
  let chatService: ChatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProfileUpdateComponent],
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
      .overrideTemplate(ProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileFormService = TestBed.inject(ProfileFormService);
    profileService = TestBed.inject(ProfileService);
    userService = TestBed.inject(UserService);
    chatService = TestBed.inject(ChatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const profile: IProfile = { id: 13324 };
      const user: IUser = { id: 3944 };
      profile.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profile query and add missing value', () => {
      const profile: IProfile = { id: 13324 };
      const others: IProfile[] = [{ id: 32255 }];
      profile.others = others;
      const profiles: IProfile[] = [{ id: 32255 }];
      profile.profiles = profiles;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [...others, ...profiles];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(expect.objectContaining),
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Chat query and add missing value', () => {
      const profile: IProfile = { id: 13324 };
      const chats: IChat[] = [{ id: 7587 }];
      profile.chats = chats;

      const chatCollection: IChat[] = [{ id: 7587 }];
      jest.spyOn(chatService, 'query').mockReturnValue(of(new HttpResponse({ body: chatCollection })));
      const additionalChats = [...chats];
      const expectedCollection: IChat[] = [...additionalChats, ...chatCollection];
      jest.spyOn(chatService, 'addChatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(chatService.query).toHaveBeenCalled();
      expect(chatService.addChatToCollectionIfMissing).toHaveBeenCalledWith(
        chatCollection,
        ...additionalChats.map(expect.objectContaining),
      );
      expect(comp.chatsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const profile: IProfile = { id: 13324 };
      const user: IUser = { id: 3944 };
      profile.user = user;
      const other: IProfile = { id: 32255 };
      profile.others = [other];
      const profile: IProfile = { id: 32255 };
      profile.profiles = [profile];
      const chat: IChat = { id: 7587 };
      profile.chats = [chat];

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.profilesSharedCollection).toContainEqual(other);
      expect(comp.profilesSharedCollection).toContainEqual(profile);
      expect(comp.chatsSharedCollection).toContainEqual(chat);
      expect(comp.profile).toEqual(profile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfile>>();
      const profile = { id: 32255 };
      jest.spyOn(profileFormService, 'getProfile').mockReturnValue(profile);
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(profileFormService.getProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileService.update).toHaveBeenCalledWith(expect.objectContaining(profile));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfile>>();
      const profile = { id: 32255 };
      jest.spyOn(profileFormService, 'getProfile').mockReturnValue({ id: null });
      jest.spyOn(profileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(profileFormService.getProfile).toHaveBeenCalled();
      expect(profileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfile>>();
      const profile = { id: 32255 };
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileService.update).toHaveBeenCalled();
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

    describe('compareChat', () => {
      it('Should forward to chatService', () => {
        const entity = { id: 7587 };
        const entity2 = { id: 26569 };
        jest.spyOn(chatService, 'compareChat');
        comp.compareChat(entity, entity2);
        expect(chatService.compareChat).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
