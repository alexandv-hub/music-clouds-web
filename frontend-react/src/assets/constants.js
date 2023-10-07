import {
  HiOutlineHashtag,
  HiOutlineHome, HiOutlineLogin, HiOutlineLogout,
  HiOutlinePhotograph,
  HiOutlineUserGroup,
  HiOutlineUsers,
} from 'react-icons/hi';
import { MdOutlinePodcasts, MdQueueMusic } from 'react-icons/md';

export const genres = [
  { title: 'Pop', value: 'POP' },
  { title: 'Hip-Hop', value: 'HIP_HOP_RAP' },
  { title: 'Dance', value: 'DANCE' },
  { title: 'Electronic', value: 'ELECTRONIC' },
  { title: 'Soul', value: 'SOUL_RNB' },
  { title: 'Alternative', value: 'ALTERNATIVE' },
  { title: 'Rock', value: 'ROCK' },
  { title: 'Latin', value: 'LATIN' },
  { title: 'Film', value: 'FILM_TV' },
  { title: 'Country', value: 'COUNTRY' },
  { title: 'Worldwide', value: 'WORLDWIDE' },
  { title: 'Reggae', value: 'REGGAE_DANCE_HALL' },
  { title: 'House', value: 'HOUSE' },
  { title: 'K-Pop', value: 'K_POP' },
];

export const links = [
  { name: 'Discover', to: '/discover', icon: HiOutlineHome },
  { name: 'Sign in', to: '/sign-in', icon: HiOutlineLogin },
  { name: 'Users management', to: '/users-management', icon: HiOutlineUsers },
  { name: 'My Tracks', to: '/my-tracks', icon: MdQueueMusic },
  { name: 'My Friends', to: '/my-friends', icon: HiOutlineUsers },
  { name: 'Podcasts', to: '/podcasts', icon: MdOutlinePodcasts },
  { name: 'Around You', to: '/around-you', icon: HiOutlinePhotograph },
  { name: 'Top Artists', to: '/top-artists', icon: HiOutlineUserGroup },
  { name: 'Top Charts', to: '/top-charts', icon: HiOutlineHashtag },
  { name: 'Logout', to: '/sign-in', icon: HiOutlineLogout },
];

export const REACT_APP_SERVER_URL = import.meta.env.VITE_REACT_APP_SERVER_URL || 'default_value';
