(function(){const t=document.createElement("link").relList;if(t&&t.supports&&t.supports("modulepreload"))return;for(const r of document.querySelectorAll('link[rel="modulepreload"]'))i(r);new MutationObserver(r=>{for(const a of r)if(a.type==="childList")for(const s of a.addedNodes)s.tagName==="LINK"&&s.rel==="modulepreload"&&i(s)}).observe(document,{childList:!0,subtree:!0});function e(r){const a={};return r.integrity&&(a.integrity=r.integrity),r.referrerPolicy&&(a.referrerPolicy=r.referrerPolicy),r.crossOrigin==="use-credentials"?a.credentials="include":r.crossOrigin==="anonymous"?a.credentials="omit":a.credentials="same-origin",a}function i(r){if(r.ep)return;r.ep=!0;const a=e(r);fetch(r.href,a)}})();const Hi=[{email:"operador@innovatecorp.com",password:"oper123",role:"operator",name:"Ana",fullName:"Ana Silva",accent:"var(--innovate-operator-blue)"},{email:"gestor@innovatecorp.com",password:"gest123",role:"manager",name:"Carlos",fullName:"Carlos Mendes",accent:"var(--innovate-manager-pink)"},{email:"lideranca@innovatecorp.com",password:"lider123",role:"leader",name:"Mariana",fullName:"Mariana Costa",accent:"var(--innovate-leader-purple)"}],aa="innovatecorp_registered_users";function Ji(){try{return JSON.parse(localStorage.getItem(aa)||"[]")}catch{return[]}}function Ai(n){localStorage.setItem(aa,JSON.stringify(n))}function na(){return[...Hi,...Ji()]}function bn({fullName:n,email:t,password:e,role:i}){const r=t.trim().toLowerCase();if(na().some(l=>l.email.toLowerCase()===r))return{ok:!1,error:"Já existe uma conta com este e-mail."};const a=n.trim().split(" ")[0]||n.trim(),s={email:r,password:e,role:i,name:a,fullName:n.trim(),accent:"var(--innovate-primary)"},o=Ji();return o.push(s),Ai(o),{ok:!0,account:s}}function yn(n,t){const e=n.trim().toLowerCase(),i=Ji(),r=i.findIndex(s=>s.email.toLowerCase()===e);if(r>=0)return i[r]={...i[r],password:t},Ai(i),{ok:!0};const a=Hi.find(s=>s.email.toLowerCase()===e);if(a){const s={...a,password:t};return Ai([...i.filter(o=>o.email.toLowerCase()!==e),s]),{ok:!0}}return{ok:!1,error:"Não encontramos esse e-mail cadastrado."}}function xn(n,t){const e=n.trim().toLowerCase(),i=na().find(r=>r.email.toLowerCase()===e);return i?t!==i.password?{ok:!1,error:"Senha incorreta para este e-mail."}:{ok:!0,account:i}:{ok:!1,error:"E-mail não autorizado para acesso."}}function R(n){return`<span class="material-symbols-rounded">${n}</span>`}function E(n){const t=document.createElement("div");return t.textContent=n==null?"":String(n),t.innerHTML}function Ki(n,t={}){const{showBadge:e=!1,onLogout:i=!1}=t;return`
    <header class="top-bar">
      <button type="button" class="icon-btn" aria-label="Menu">${R("menu")}</button>
      <h2>${E(n)}</h2>
      <div class="top-bar-actions">
        <button type="button" class="icon-btn theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">
          <span class="theme-icon">◐</span>
        </button>
        <button type="button" class="icon-btn ${e?"badge-dot":""}" data-action="notifications" aria-label="Notificações">
          ${R("notifications")}
        </button>
        ${i?`<button type="button" class="icon-btn" data-action="logout" aria-label="Sair">${R("logout")}</button>`:""}
      </div>
    </header>
  `}function Mt(n,t=""){return`
    <header class="overlay-top">
      <button type="button" class="icon-btn" data-action="back" aria-label="Voltar">${R("arrow_back")}</button>
      <h2>${E(n)}</h2>
      <div class="top-bar-actions">${t}</div>
    </header>
  `}function Qi(n,t){return`
    <nav class="bottom-nav">
      ${n.map(e=>`
        <button type="button" class="nav-item ${e.id===t?"active":""}" data-tab="${e.id}">
          ${R(e.icon)}
          <span>${E(e.label)}</span>
        </button>
      `).join("")}
    </nav>
  `}function Et(n,t,e=""){return`
    <div class="kpi-card">
      <div class="label">${E(n)}</div>
      <div class="value">${E(t)}</div>
      ${e?`<div class="trend">${E(e)}</div>`:""}
    </div>
  `}function sa(n,t=[]){if(!n||n.length===0)return'<p class="muted-text">Sem dados.</p>';const e=Math.max(...n,1);return`
    <div class="bar-chart">
      ${n.map((i,r)=>{const a=Math.max(i/e*100,6),s=t[r]||"";return`
            <div class="bar-col">
              <div class="bar-stack">
                <div class="bar" style="height:${a}%" data-value="${i}"></div>
              </div>
              ${s?`<span class="bar-label" title="${E(s)}">${E(s)}</span>`:""}
            </div>
          `}).join("")}
    </div>
  `}const $n={operator:"Operador",manager:"Gestor",leader:"Líder"};function wn(n){return`<span class="role-tag role-${n}">${$n[n]||n}</span>`}function mi(n,t,e=44){const i=(n||"?").trim().charAt(0).toUpperCase();return`<div class="collab-avatar" style="width:${e}px;height:${e}px;background:${t||"#6C5CE7"};font-size:${Math.floor(e/2.6)}px">${E(i)}</div>`}function hi(n){switch(n){case"manager":return"#EC4899";case"leader":return"#7C3AED";default:return"#3B82F6"}}function Zi(n,t="",e=""){return`
    <div class="section-header">
      <span class="section-title">${E(n)}</span>
      ${t?`<button type="button" class="btn-ghost" data-action="${e}">${E(t)} <span class="material-symbols-rounded" style="font-size:18px;vertical-align:-3px">arrow_forward</span></button>`:""}
    </div>
  `}function it(n,t="inbox"){return`
    <div class="empty-state-card">
      ${R(t)}
      <strong>Nada por aqui ainda</strong>
      <p style="margin:0;font-size:0.85rem">${E(n)}</p>
    </div>
  `}function Pt(n){if(!n)return"";const t=Date.now()-n,e=60*1e3,i=60*e,r=24*i;return t<e?"agora":t<i?`há ${Math.floor(t/e)} min`:t<r?`há ${Math.floor(t/i)} h`:`há ${Math.floor(t/r)} dia(s)`}function tr(n,t){n.querySelectorAll("[data-tab]").forEach(e=>{e.addEventListener("click",()=>t(e.dataset.tab))})}function Cr(n){document.documentElement.dataset.role=n||""}function Sn(){const n=localStorage.getItem("innovatecorp_color_theme")||"light";document.documentElement.dataset.theme=n}function kn(){const t=(document.documentElement.dataset.theme==="dark"?"dark":"light")==="dark"?"light":"dark";return document.documentElement.dataset.theme=t,localStorage.setItem("innovatecorp_color_theme",t),t}const En={operator:"Operador",manager:"Gestor",leader:"Líder"};function Ce(n){return En[n]||""}const Or={operator:["#3B82F6","#6C5CE7","#10B981","#F59E0B","#1A1A2E","#EC4899"],manager:["#EC4899","#7C3AED","#3B82F6","#10B981","#F59E0B","#1A1A2E"],leader:["#7C3AED","#4F1D96","#3B82F6","#EC4899","#10B981","#1A1A2E"]};function er(n){return Or[n]||Or.operator}function ge(n,t=96){const e=n.avatarColor||"#6C5CE7";return n.avatar?`<div class="profile-avatar" style="width:${t}px;height:${t}px;background:${e}"><img src="${n.avatar}" alt="" /></div>`:`<div class="profile-avatar" style="width:${t}px;height:${t}px;background:${e};font-size:${Math.floor(t/2.6)}px">${E((n.name||n.fullName||"?").charAt(0).toUpperCase())}</div>`}function zt(n){if(n===void 0)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return n}function oa(n,t){n.prototype=Object.create(t.prototype),n.prototype.constructor=n,n.__proto__=t}/*!
 * GSAP 3.15.0
 * https://gsap.com
 *
 * @license Copyright 2008-2026, GreenSock. All rights reserved.
 * Subject to the terms at https://gsap.com/standard-license
 * @author: Jack Doyle, jack@greensock.com
*/var $t={autoSleep:120,force3D:"auto",nullTargetWarn:1,units:{lineHeight:""}},Oe={duration:.5,overwrite:!1,delay:0},ir,rt,Y,Tt=1e8,B=1/Tt,Ci=Math.PI*2,Tn=Ci/4,An=0,la=Math.sqrt,Cn=Math.cos,On=Math.sin,et=function(t){return typeof t=="string"},J=function(t){return typeof t=="function"},Ft=function(t){return typeof t=="number"},rr=function(t){return typeof t>"u"},qt=function(t){return typeof t=="object"},ft=function(t){return t!==!1},ar=function(){return typeof window<"u"},Ye=function(t){return J(t)||et(t)},ca=typeof ArrayBuffer=="function"&&ArrayBuffer.isView||function(){},ot=Array.isArray,Pn=/random\([^)]+\)/g,Dn=/,\s*/g,Pr=/(?:-?\.?\d|\.)+/gi,da=/[-+=.]*\d+[.e\-+]*\d*[e\-+]*\d*/g,ue=/[-+=.]*\d+[.e-]*\d*[a-z%]*/g,gi=/[-+=.]*\d+\.?\d*(?:e-|e\+)?\d*/gi,ua=/[+-]=-?[.\d]+/,Ln=/[^,'"\[\]\s]+/gi,In=/^[+\-=e\s\d]*\d+[.\d]*([a-z]*|%)\s*$/i,W,Lt,Oi,nr,wt={},Ke={},fa,pa=function(t){return(Ke=ve(t,wt))&&gt},sr=function(t,e){return console.warn("Invalid property",t,"set to",e,"Missing plugin? gsap.registerPlugin()")},Pe=function(t,e){return!e&&console.warn(t)},ma=function(t,e){return t&&(wt[t]=e)&&Ke&&(Ke[t]=e)||wt},De=function(){return 0},Rn={suppressEvents:!0,isStart:!0,kill:!1},Xe={suppressEvents:!0,kill:!1},Mn={suppressEvents:!0},or={},Xt=[],Pi={},ha,_t={},vi={},Dr=30,We=[],lr="",cr=function(t){var e=t[0],i,r;if(qt(e)||J(e)||(t=[t]),!(i=(e._gsap||{}).harness)){for(r=We.length;r--&&!We[r].targetTest(e););i=We[r]}for(r=t.length;r--;)t[r]&&(t[r]._gsap||(t[r]._gsap=new Na(t[r],i)))||t.splice(r,1);return t},ne=function(t){return t._gsap||cr(At(t))[0]._gsap},ga=function(t,e,i){return(i=t[e])&&J(i)?t[e]():rr(i)&&t.getAttribute&&t.getAttribute(e)||i},pt=function(t,e){return(t=t.split(",")).forEach(e)||t},Q=function(t){return Math.round(t*1e5)/1e5||0},X=function(t){return Math.round(t*1e7)/1e7||0},pe=function(t,e){var i=e.charAt(0),r=parseFloat(e.substr(2));return t=parseFloat(t),i==="+"?t+r:i==="-"?t-r:i==="*"?t*r:t/r},Nn=function(t,e){for(var i=e.length,r=0;t.indexOf(e[r])<0&&++r<i;);return r<i},Qe=function(){var t=Xt.length,e=Xt.slice(0),i,r;for(Pi={},Xt.length=0,i=0;i<t;i++)r=e[i],r&&r._lazy&&(r.render(r._lazy[0],r._lazy[1],!0)._lazy=0)},dr=function(t){return!!(t._initted||t._startAt||t.add)},va=function(t,e,i,r){Xt.length&&!rt&&Qe(),t.render(e,i,!!(rt&&e<0&&dr(t))),Xt.length&&!rt&&Qe()},_a=function(t){var e=parseFloat(t);return(e||e===0)&&(t+"").match(Ln).length<2?e:et(t)?t.trim():t},ba=function(t){return t},St=function(t,e){for(var i in e)i in t||(t[i]=e[i]);return t},qn=function(t){return function(e,i){for(var r in i)r in e||r==="duration"&&t||r==="ease"||(e[r]=i[r])}},ve=function(t,e){for(var i in e)t[i]=e[i];return t},Lr=function n(t,e){for(var i in e)i!=="__proto__"&&i!=="constructor"&&i!=="prototype"&&(t[i]=qt(e[i])?n(t[i]||(t[i]={}),e[i]):e[i]);return t},Ze=function(t,e){var i={},r;for(r in t)r in e||(i[r]=t[r]);return i},Ee=function(t){var e=t.parent||W,i=t.keyframes?qn(ot(t.keyframes)):St;if(ft(t.inherit))for(;e;)i(t,e.vars.defaults),e=e.parent||e._dp;return t},zn=function(t,e){for(var i=t.length,r=i===e.length;r&&i--&&t[i]===e[i];);return i<0},ya=function(t,e,i,r,a){var s=t[r],o;if(a)for(o=e[a];s&&s[a]>o;)s=s._prev;return s?(e._next=s._next,s._next=e):(e._next=t[i],t[i]=e),e._next?e._next._prev=e:t[r]=e,e._prev=s,e.parent=e._dp=t,e},si=function(t,e,i,r){i===void 0&&(i="_first"),r===void 0&&(r="_last");var a=e._prev,s=e._next;a?a._next=s:t[i]===e&&(t[i]=s),s?s._prev=a:t[r]===e&&(t[r]=a),e._next=e._prev=e.parent=null},Ht=function(t,e){t.parent&&(!e||t.parent.autoRemoveChildren)&&t.parent.remove&&t.parent.remove(t),t._act=0},se=function(t,e){if(t&&(!e||e._end>t._dur||e._start<0))for(var i=t;i;)i._dirty=1,i=i.parent;return t},jn=function(t){for(var e=t.parent;e&&e.parent;)e._dirty=1,e.totalDuration(),e=e.parent;return t},Di=function(t,e,i,r){return t._startAt&&(rt?t._startAt.revert(Xe):t.vars.immediateRender&&!t.vars.autoRevert||t._startAt.render(e,!0,r))},Fn=function n(t){return!t||t._ts&&n(t.parent)},Ir=function(t){return t._repeat?_e(t._tTime,t=t.duration()+t._rDelay)*t:0},_e=function(t,e){var i=Math.floor(t=X(t/e));return t&&i===t?i-1:i},ti=function(t,e){return(t-e._start)*e._ts+(e._ts>=0?0:e._dirty?e.totalDuration():e._tDur)},oi=function(t){return t._end=X(t._start+(t._tDur/Math.abs(t._ts||t._rts||B)||0))},li=function(t,e){var i=t._dp;return i&&i.smoothChildTiming&&t._ts&&(t._start=X(i._time-(t._ts>0?e/t._ts:((t._dirty?t.totalDuration():t._tDur)-e)/-t._ts)),oi(t),i._dirty||se(i,t)),t},xa=function(t,e){var i;if((e._time||!e._dur&&e._initted||e._start<t._time&&(e._dur||!e.add))&&(i=ti(t.rawTime(),e),(!e._dur||je(0,e.totalDuration(),i)-e._tTime>B)&&e.render(i,!0)),se(t,e)._dp&&t._initted&&t._time>=t._dur&&t._ts){if(t._dur<t.duration())for(i=t;i._dp;)i.rawTime()>=0&&i.totalTime(i._tTime),i=i._dp;t._zTime=-B}},It=function(t,e,i,r){return e.parent&&Ht(e),e._start=X((Ft(i)?i:i||t!==W?kt(t,i,e):t._time)+e._delay),e._end=X(e._start+(e.totalDuration()/Math.abs(e.timeScale())||0)),ya(t,e,"_first","_last",t._sort?"_start":0),Li(e)||(t._recent=e),r||xa(t,e),t._ts<0&&li(t,t._tTime),t},$a=function(t,e){return(wt.ScrollTrigger||sr("scrollTrigger",e))&&wt.ScrollTrigger.create(e,t)},wa=function(t,e,i,r,a){if(fr(t,e,a),!t._initted)return 1;if(!i&&t._pt&&!rt&&(t._dur&&t.vars.lazy!==!1||!t._dur&&t.vars.lazy)&&ha!==bt.frame)return Xt.push(t),t._lazy=[a,r],1},Bn=function n(t){var e=t.parent;return e&&e._ts&&e._initted&&!e._lock&&(e.rawTime()<0||n(e))},Li=function(t){var e=t.data;return e==="isFromStart"||e==="isStart"},Vn=function(t,e,i,r){var a=t.ratio,s=e<0||!e&&(!t._start&&Bn(t)&&!(!t._initted&&Li(t))||(t._ts<0||t._dp._ts<0)&&!Li(t))?0:1,o=t._rDelay,l=0,c,d,f;if(o&&t._repeat&&(l=je(0,t._tDur,e),d=_e(l,o),t._yoyo&&d&1&&(s=1-s),d!==_e(t._tTime,o)&&(a=1-s,t.vars.repeatRefresh&&t._initted&&t.invalidate())),s!==a||rt||r||t._zTime===B||!e&&t._zTime){if(!t._initted&&wa(t,e,r,i,l))return;for(f=t._zTime,t._zTime=e||(i?B:0),i||(i=e&&!f),t.ratio=s,t._from&&(s=1-s),t._time=0,t._tTime=l,c=t._pt;c;)c.r(s,c.d),c=c._next;e<0&&Di(t,e,i,!0),t._onUpdate&&!i&&yt(t,"onUpdate"),l&&t._repeat&&!i&&t.parent&&yt(t,"onRepeat"),(e>=t._tDur||e<0)&&t.ratio===s&&(s&&Ht(t,1),!i&&!rt&&(yt(t,s?"onComplete":"onReverseComplete",!0),t._prom&&t._prom()))}else t._zTime||(t._zTime=e)},Un=function(t,e,i){var r;if(i>e)for(r=t._first;r&&r._start<=i;){if(r.data==="isPause"&&r._start>e)return r;r=r._next}else for(r=t._last;r&&r._start>=i;){if(r.data==="isPause"&&r._start<e)return r;r=r._prev}},be=function(t,e,i,r){var a=t._repeat,s=X(e)||0,o=t._tTime/t._tDur;return o&&!r&&(t._time*=s/t._dur),t._dur=s,t._tDur=a?a<0?1e10:X(s*(a+1)+t._rDelay*a):s,o>0&&!r&&li(t,t._tTime=t._tDur*o),t.parent&&oi(t),i||se(t.parent,t),t},Rr=function(t){return t instanceof ut?se(t):be(t,t._dur)},Yn={_start:0,endTime:De,totalDuration:De},kt=function n(t,e,i){var r=t.labels,a=t._recent||Yn,s=t.duration()>=Tt?a.endTime(!1):t._dur,o,l,c;return et(e)&&(isNaN(e)||e in r)?(l=e.charAt(0),c=e.substr(-1)==="%",o=e.indexOf("="),l==="<"||l===">"?(o>=0&&(e=e.replace(/=/,"")),(l==="<"?a._start:a.endTime(a._repeat>=0))+(parseFloat(e.substr(1))||0)*(c?(o<0?a:i).totalDuration()/100:1)):o<0?(e in r||(r[e]=s),r[e]):(l=parseFloat(e.charAt(o-1)+e.substr(o+1)),c&&i&&(l=l/100*(ot(i)?i[0]:i).totalDuration()),o>1?n(t,e.substr(0,o-1),i)+l:s+l)):e==null?s:+e},Te=function(t,e,i){var r=Ft(e[1]),a=(r?2:1)+(t<2?0:1),s=e[a],o,l;if(r&&(s.duration=e[1]),s.parent=i,t){for(o=s,l=i;l&&!("immediateRender"in o);)o=l.vars.defaults||{},l=ft(l.vars.inherit)&&l.parent;s.immediateRender=ft(o.immediateRender),t<2?s.runBackwards=1:s.startAt=e[a-1]}return new tt(e[0],s,e[a+1])},Zt=function(t,e){return t||t===0?e(t):e},je=function(t,e,i){return i<t?t:i>e?e:i},st=function(t,e){return!et(t)||!(e=In.exec(t))?"":e[1]},Gn=function(t,e,i){return Zt(i,function(r){return je(t,e,r)})},Ii=[].slice,Sa=function(t,e){return t&&qt(t)&&"length"in t&&(!e&&!t.length||t.length-1 in t&&qt(t[0]))&&!t.nodeType&&t!==Lt},Xn=function(t,e,i){return i===void 0&&(i=[]),t.forEach(function(r){var a;return et(r)&&!e||Sa(r,1)?(a=i).push.apply(a,At(r)):i.push(r)})||i},At=function(t,e,i){return Y&&!e&&Y.selector?Y.selector(t):et(t)&&!i&&(Oi||!ye())?Ii.call((e||nr).querySelectorAll(t),0):ot(t)?Xn(t,i):Sa(t)?Ii.call(t,0):t?[t]:[]},Ri=function(t){return t=At(t)[0]||Pe("Invalid scope")||{},function(e){var i=t.current||t.nativeElement||t;return At(e,i.querySelectorAll?i:i===t?Pe("Invalid scope")||nr.createElement("div"):t)}},ka=function(t){return t.sort(function(){return .5-Math.random()})},Ea=function(t){if(J(t))return t;var e=qt(t)?t:{each:t},i=oe(e.ease),r=e.from||0,a=parseFloat(e.base)||0,s={},o=r>0&&r<1,l=isNaN(r)||o,c=e.axis,d=r,f=r;return et(r)?d=f={center:.5,edges:.5,end:1}[r]||0:!o&&l&&(d=r[0],f=r[1]),function(p,m,h){var u=(h||e).length,v=s[u],y,b,k,T,w,D,A,O,_;if(!v){if(_=e.grid==="auto"?0:(e.grid||[1,Tt])[1],!_){for(A=-Tt;A<(A=h[_++].getBoundingClientRect().left)&&_<u;);_<u&&_--}for(v=s[u]=[],y=l?Math.min(_,u)*d-.5:r%_,b=_===Tt?0:l?u*f/_-.5:r/_|0,A=0,O=Tt,D=0;D<u;D++)k=D%_-y,T=b-(D/_|0),v[D]=w=c?Math.abs(c==="y"?T:k):la(k*k+T*T),w>A&&(A=w),w<O&&(O=w);r==="random"&&ka(v),v.max=A-O,v.min=O,v.v=u=(parseFloat(e.amount)||parseFloat(e.each)*(_>u?u-1:c?c==="y"?u/_:_:Math.max(_,u/_))||0)*(r==="edges"?-1:1),v.b=u<0?a-u:a,v.u=st(e.amount||e.each)||0,i=i&&u<0?ss(i):i}return u=(v[p]-v.min)/v.max||0,X(v.b+(i?i(u):u)*v.v)+v.u}},Mi=function(t){var e=Math.pow(10,((t+"").split(".")[1]||"").length);return function(i){var r=X(Math.round(parseFloat(i)/t)*t*e);return(r-r%1)/e+(Ft(i)?0:st(i))}},Ta=function(t,e){var i=ot(t),r,a;return!i&&qt(t)&&(r=i=t.radius||Tt,t.values?(t=At(t.values),(a=!Ft(t[0]))&&(r*=r)):t=Mi(t.increment)),Zt(e,i?J(t)?function(s){return a=t(s),Math.abs(a-s)<=r?a:s}:function(s){for(var o=parseFloat(a?s.x:s),l=parseFloat(a?s.y:0),c=Tt,d=0,f=t.length,p,m;f--;)a?(p=t[f].x-o,m=t[f].y-l,p=p*p+m*m):p=Math.abs(t[f]-o),p<c&&(c=p,d=f);return d=!r||c<=r?t[d]:s,a||d===s||Ft(s)?d:d+st(s)}:Mi(t))},Aa=function(t,e,i,r){return Zt(ot(t)?!e:i===!0?!!(i=0):!r,function(){return ot(t)?t[~~(Math.random()*t.length)]:(i=i||1e-5)&&(r=i<1?Math.pow(10,(i+"").length-2):1)&&Math.floor(Math.round((t-i/2+Math.random()*(e-t+i*.99))/i)*i*r)/r})},Wn=function(){for(var t=arguments.length,e=new Array(t),i=0;i<t;i++)e[i]=arguments[i];return function(r){return e.reduce(function(a,s){return s(a)},r)}},Hn=function(t,e){return function(i){return t(parseFloat(i))+(e||st(i))}},Jn=function(t,e,i){return Oa(t,e,0,1,i)},Ca=function(t,e,i){return Zt(i,function(r){return t[~~e(r)]})},Kn=function n(t,e,i){var r=e-t;return ot(t)?Ca(t,n(0,t.length),e):Zt(i,function(a){return(r+(a-t)%r)%r+t})},Qn=function n(t,e,i){var r=e-t,a=r*2;return ot(t)?Ca(t,n(0,t.length-1),e):Zt(i,function(s){return s=(a+(s-t)%a)%a||0,t+(s>r?a-s:s)})},Le=function(t){return t.replace(Pn,function(e){var i=e.indexOf("[")+1,r=e.substring(i||7,i?e.indexOf("]"):e.length-1).split(Dn);return Aa(i?r:+r[0],i?0:+r[1],+r[2]||1e-5)})},Oa=function(t,e,i,r,a){var s=e-t,o=r-i;return Zt(a,function(l){return i+((l-t)/s*o||0)})},Zn=function n(t,e,i,r){var a=isNaN(t+e)?0:function(m){return(1-m)*t+m*e};if(!a){var s=et(t),o={},l,c,d,f,p;if(i===!0&&(r=1)&&(i=null),s)t={p:t},e={p:e};else if(ot(t)&&!ot(e)){for(d=[],f=t.length,p=f-2,c=1;c<f;c++)d.push(n(t[c-1],t[c]));f--,a=function(h){h*=f;var u=Math.min(p,~~h);return d[u](h-u)},i=e}else r||(t=ve(ot(t)?[]:{},t));if(!d){for(l in e)ur.call(o,t,l,"get",e[l]);a=function(h){return hr(h,o)||(s?t.p:t)}}}return Zt(i,a)},Mr=function(t,e,i){var r=t.labels,a=Tt,s,o,l;for(s in r)o=r[s]-e,o<0==!!i&&o&&a>(o=Math.abs(o))&&(l=s,a=o);return l},yt=function(t,e,i){var r=t.vars,a=r[e],s=Y,o=t._ctx,l,c,d;if(a)return l=r[e+"Params"],c=r.callbackScope||t,i&&Xt.length&&Qe(),o&&(Y=o),d=l?a.apply(c,l):a.call(c),Y=s,d},Se=function(t){return Ht(t),t.scrollTrigger&&t.scrollTrigger.kill(!!rt),t.progress()<1&&yt(t,"onInterrupt"),t},fe,Pa=[],Da=function(t){if(t)if(t=!t.name&&t.default||t,ar()||t.headless){var e=t.name,i=J(t),r=e&&!i&&t.init?function(){this._props=[]}:t,a={init:De,render:hr,add:ur,kill:gs,modifier:hs,rawVars:0},s={targetTest:0,get:0,getSetter:mr,aliases:{},register:0};if(ye(),t!==r){if(_t[e])return;St(r,St(Ze(t,a),s)),ve(r.prototype,ve(a,Ze(t,s))),_t[r.prop=e]=r,t.targetTest&&(We.push(r),or[e]=1),e=(e==="css"?"CSS":e.charAt(0).toUpperCase()+e.substr(1))+"Plugin"}ma(e,r),t.register&&t.register(gt,r,mt)}else Pa.push(t)},F=255,ke={aqua:[0,F,F],lime:[0,F,0],silver:[192,192,192],black:[0,0,0],maroon:[128,0,0],teal:[0,128,128],blue:[0,0,F],navy:[0,0,128],white:[F,F,F],olive:[128,128,0],yellow:[F,F,0],orange:[F,165,0],gray:[128,128,128],purple:[128,0,128],green:[0,128,0],red:[F,0,0],pink:[F,192,203],cyan:[0,F,F],transparent:[F,F,F,0]},_i=function(t,e,i){return t+=t<0?1:t>1?-1:0,(t*6<1?e+(i-e)*t*6:t<.5?i:t*3<2?e+(i-e)*(2/3-t)*6:e)*F+.5|0},La=function(t,e,i){var r=t?Ft(t)?[t>>16,t>>8&F,t&F]:0:ke.black,a,s,o,l,c,d,f,p,m,h;if(!r){if(t.substr(-1)===","&&(t=t.substr(0,t.length-1)),ke[t])r=ke[t];else if(t.charAt(0)==="#"){if(t.length<6&&(a=t.charAt(1),s=t.charAt(2),o=t.charAt(3),t="#"+a+a+s+s+o+o+(t.length===5?t.charAt(4)+t.charAt(4):"")),t.length===9)return r=parseInt(t.substr(1,6),16),[r>>16,r>>8&F,r&F,parseInt(t.substr(7),16)/255];t=parseInt(t.substr(1),16),r=[t>>16,t>>8&F,t&F]}else if(t.substr(0,3)==="hsl"){if(r=h=t.match(Pr),!e)l=+r[0]%360/360,c=+r[1]/100,d=+r[2]/100,s=d<=.5?d*(c+1):d+c-d*c,a=d*2-s,r.length>3&&(r[3]*=1),r[0]=_i(l+1/3,a,s),r[1]=_i(l,a,s),r[2]=_i(l-1/3,a,s);else if(~t.indexOf("="))return r=t.match(da),i&&r.length<4&&(r[3]=1),r}else r=t.match(Pr)||ke.transparent;r=r.map(Number)}return e&&!h&&(a=r[0]/F,s=r[1]/F,o=r[2]/F,f=Math.max(a,s,o),p=Math.min(a,s,o),d=(f+p)/2,f===p?l=c=0:(m=f-p,c=d>.5?m/(2-f-p):m/(f+p),l=f===a?(s-o)/m+(s<o?6:0):f===s?(o-a)/m+2:(a-s)/m+4,l*=60),r[0]=~~(l+.5),r[1]=~~(c*100+.5),r[2]=~~(d*100+.5)),i&&r.length<4&&(r[3]=1),r},Ia=function(t){var e=[],i=[],r=-1;return t.split(Wt).forEach(function(a){var s=a.match(ue)||[];e.push.apply(e,s),i.push(r+=s.length+1)}),e.c=i,e},Nr=function(t,e,i){var r="",a=(t+r).match(Wt),s=e?"hsla(":"rgba(",o=0,l,c,d,f;if(!a)return t;if(a=a.map(function(p){return(p=La(p,e,1))&&s+(e?p[0]+","+p[1]+"%,"+p[2]+"%,"+p[3]:p.join(","))+")"}),i&&(d=Ia(t),l=i.c,l.join(r)!==d.c.join(r)))for(c=t.replace(Wt,"1").split(ue),f=c.length-1;o<f;o++)r+=c[o]+(~l.indexOf(o)?a.shift()||s+"0,0,0,0)":(d.length?d:a.length?a:i).shift());if(!c)for(c=t.split(Wt),f=c.length-1;o<f;o++)r+=c[o]+a[o];return r+c[f]},Wt=(function(){var n="(?:\\b(?:(?:rgb|rgba|hsl|hsla)\\(.+?\\))|\\B#(?:[0-9a-f]{3,4}){1,2}\\b",t;for(t in ke)n+="|"+t+"\\b";return new RegExp(n+")","gi")})(),ts=/hsl[a]?\(/,Ra=function(t){var e=t.join(" "),i;if(Wt.lastIndex=0,Wt.test(e))return i=ts.test(e),t[1]=Nr(t[1],i),t[0]=Nr(t[0],i,Ia(t[1])),!0},Ie,bt=(function(){var n=Date.now,t=500,e=33,i=n(),r=i,a=1e3/240,s=a,o=[],l,c,d,f,p,m,h=function u(v){var y=n()-r,b=v===!0,k,T,w,D;if((y>t||y<0)&&(i+=y-e),r+=y,w=r-i,k=w-s,(k>0||b)&&(D=++f.frame,p=w-f.time*1e3,f.time=w=w/1e3,s+=k+(k>=a?4:a-k),T=1),b||(l=c(u)),T)for(m=0;m<o.length;m++)o[m](w,p,D,v)};return f={time:0,frame:0,tick:function(){h(!0)},deltaRatio:function(v){return p/(1e3/(v||60))},wake:function(){fa&&(!Oi&&ar()&&(Lt=Oi=window,nr=Lt.document||{},wt.gsap=gt,(Lt.gsapVersions||(Lt.gsapVersions=[])).push(gt.version),pa(Ke||Lt.GreenSockGlobals||!Lt.gsap&&Lt||{}),Pa.forEach(Da)),d=typeof requestAnimationFrame<"u"&&requestAnimationFrame,l&&f.sleep(),c=d||function(v){return setTimeout(v,s-f.time*1e3+1|0)},Ie=1,h(2))},sleep:function(){(d?cancelAnimationFrame:clearTimeout)(l),Ie=0,c=De},lagSmoothing:function(v,y){t=v||1/0,e=Math.min(y||33,t)},fps:function(v){a=1e3/(v||240),s=f.time*1e3+a},add:function(v,y,b){var k=y?function(T,w,D,A){v(T,w,D,A),f.remove(k)}:v;return f.remove(v),o[b?"unshift":"push"](k),ye(),k},remove:function(v,y){~(y=o.indexOf(v))&&o.splice(y,1)&&m>=y&&m--},_listeners:o},f})(),ye=function(){return!Ie&&bt.wake()},j={},es=/^[\d.\-M][\d.\-,\s]/,is=/["']/g,rs=function(t){for(var e={},i=t.substr(1,t.length-3).split(":"),r=i[0],a=1,s=i.length,o,l,c;a<s;a++)l=i[a],o=a!==s-1?l.lastIndexOf(","):l.length,c=l.substr(0,o),e[r]=isNaN(c)?c.replace(is,"").trim():+c,r=l.substr(o+1).trim();return e},as=function(t){var e=t.indexOf("(")+1,i=t.indexOf(")"),r=t.indexOf("(",e);return t.substring(e,~r&&r<i?t.indexOf(")",i+1):i)},ns=function(t){var e=(t+"").split("("),i=j[e[0]];return i&&e.length>1&&i.config?i.config.apply(null,~t.indexOf("{")?[rs(e[1])]:as(t).split(",").map(_a)):j._CE&&es.test(t)?j._CE("",t):i},ss=function(t){return function(e){return 1-t(1-e)}},oe=function(t,e){return t&&(J(t)?t:j[t]||ns(t))||e},ce=function(t,e,i,r){i===void 0&&(i=function(l){return 1-e(1-l)}),r===void 0&&(r=function(l){return l<.5?e(l*2)/2:1-e((1-l)*2)/2});var a={easeIn:e,easeOut:i,easeInOut:r},s;return pt(t,function(o){j[o]=wt[o]=a,j[s=o.toLowerCase()]=i;for(var l in a)j[s+(l==="easeIn"?".in":l==="easeOut"?".out":".inOut")]=j[o+"."+l]=a[l]}),a},Ma=function(t){return function(e){return e<.5?(1-t(1-e*2))/2:.5+t((e-.5)*2)/2}},bi=function n(t,e,i){var r=e>=1?e:1,a=(i||(t?.3:.45))/(e<1?e:1),s=a/Ci*(Math.asin(1/r)||0),o=function(d){return d===1?1:r*Math.pow(2,-10*d)*On((d-s)*a)+1},l=t==="out"?o:t==="in"?function(c){return 1-o(1-c)}:Ma(o);return a=Ci/a,l.config=function(c,d){return n(t,c,d)},l},yi=function n(t,e){e===void 0&&(e=1.70158);var i=function(s){return s?--s*s*((e+1)*s+e)+1:0},r=t==="out"?i:t==="in"?function(a){return 1-i(1-a)}:Ma(i);return r.config=function(a){return n(t,a)},r};pt("Linear,Quad,Cubic,Quart,Quint,Strong",function(n,t){var e=t<5?t+1:t;ce(n+",Power"+(e-1),t?function(i){return Math.pow(i,e)}:function(i){return i},function(i){return 1-Math.pow(1-i,e)},function(i){return i<.5?Math.pow(i*2,e)/2:1-Math.pow((1-i)*2,e)/2})});j.Linear.easeNone=j.none=j.Linear.easeIn;ce("Elastic",bi("in"),bi("out"),bi());(function(n,t){var e=1/t,i=2*e,r=2.5*e,a=function(o){return o<e?n*o*o:o<i?n*Math.pow(o-1.5/t,2)+.75:o<r?n*(o-=2.25/t)*o+.9375:n*Math.pow(o-2.625/t,2)+.984375};ce("Bounce",function(s){return 1-a(1-s)},a)})(7.5625,2.75);ce("Expo",function(n){return Math.pow(2,10*(n-1))*n+n*n*n*n*n*n*(1-n)});ce("Circ",function(n){return-(la(1-n*n)-1)});ce("Sine",function(n){return n===1?1:-Cn(n*Tn)+1});ce("Back",yi("in"),yi("out"),yi());j.SteppedEase=j.steps=wt.SteppedEase={config:function(t,e){t===void 0&&(t=1);var i=1/t,r=t+(e?0:1),a=e?1:0,s=1-B;return function(o){return((r*je(0,s,o)|0)+a)*i}}};Oe.ease=j["quad.out"];pt("onComplete,onUpdate,onStart,onRepeat,onReverseComplete,onInterrupt",function(n){return lr+=n+","+n+"Params,"});var Na=function(t,e){this.id=An++,t._gsap=this,this.target=t,this.harness=e,this.get=e?e.get:ga,this.set=e?e.getSetter:mr},Re=(function(){function n(e){this.vars=e,this._delay=+e.delay||0,(this._repeat=e.repeat===1/0?-2:e.repeat||0)&&(this._rDelay=e.repeatDelay||0,this._yoyo=!!e.yoyo||!!e.yoyoEase),this._ts=1,be(this,+e.duration,1,1),this.data=e.data,Y&&(this._ctx=Y,Y.data.push(this)),Ie||bt.wake()}var t=n.prototype;return t.delay=function(i){return i||i===0?(this.parent&&this.parent.smoothChildTiming&&this.startTime(this._start+i-this._delay),this._delay=i,this):this._delay},t.duration=function(i){return arguments.length?this.totalDuration(this._repeat>0?i+(i+this._rDelay)*this._repeat:i):this.totalDuration()&&this._dur},t.totalDuration=function(i){return arguments.length?(this._dirty=0,be(this,this._repeat<0?i:(i-this._repeat*this._rDelay)/(this._repeat+1))):this._tDur},t.totalTime=function(i,r){if(ye(),!arguments.length)return this._tTime;var a=this._dp;if(a&&a.smoothChildTiming&&this._ts){for(li(this,i),!a._dp||a.parent||xa(a,this);a&&a.parent;)a.parent._time!==a._start+(a._ts>=0?a._tTime/a._ts:(a.totalDuration()-a._tTime)/-a._ts)&&a.totalTime(a._tTime,!0),a=a.parent;!this.parent&&this._dp.autoRemoveChildren&&(this._ts>0&&i<this._tDur||this._ts<0&&i>0||!this._tDur&&!i)&&It(this._dp,this,this._start-this._delay)}return(this._tTime!==i||!this._dur&&!r||this._initted&&Math.abs(this._zTime)===B||!this._initted&&this._dur&&i||!i&&!this._initted&&(this.add||this._ptLookup))&&(this._ts||(this._pTime=i),va(this,i,r)),this},t.time=function(i,r){return arguments.length?this.totalTime(Math.min(this.totalDuration(),i+Ir(this))%(this._dur+this._rDelay)||(i?this._dur:0),r):this._time},t.totalProgress=function(i,r){return arguments.length?this.totalTime(this.totalDuration()*i,r):this.totalDuration()?Math.min(1,this._tTime/this._tDur):this.rawTime()>=0&&this._initted?1:0},t.progress=function(i,r){return arguments.length?this.totalTime(this.duration()*(this._yoyo&&!(this.iteration()&1)?1-i:i)+Ir(this),r):this.duration()?Math.min(1,this._time/this._dur):this.rawTime()>0?1:0},t.iteration=function(i,r){var a=this.duration()+this._rDelay;return arguments.length?this.totalTime(this._time+(i-1)*a,r):this._repeat?_e(this._tTime,a)+1:1},t.timeScale=function(i,r){if(!arguments.length)return this._rts===-B?0:this._rts;if(this._rts===i)return this;var a=this.parent&&this._ts?ti(this.parent._time,this):this._tTime;return this._rts=+i||0,this._ts=this._ps||i===-B?0:this._rts,this.totalTime(je(-Math.abs(this._delay),this.totalDuration(),a),r!==!1),oi(this),jn(this)},t.paused=function(i){return arguments.length?(this._ps!==i&&(this._ps=i,i?(this._pTime=this._tTime||Math.max(-this._delay,this.rawTime()),this._ts=this._act=0):(ye(),this._ts=this._rts,this.totalTime(this.parent&&!this.parent.smoothChildTiming?this.rawTime():this._tTime||this._pTime,this.progress()===1&&Math.abs(this._zTime)!==B&&(this._tTime-=B)))),this):this._ps},t.startTime=function(i){if(arguments.length){this._start=X(i);var r=this.parent||this._dp;return r&&(r._sort||!this.parent)&&It(r,this,this._start-this._delay),this}return this._start},t.endTime=function(i){return this._start+(ft(i)?this.totalDuration():this.duration())/Math.abs(this._ts||1)},t.rawTime=function(i){var r=this.parent||this._dp;return r?i&&(!this._ts||this._repeat&&this._time&&this.totalProgress()<1)?this._tTime%(this._dur+this._rDelay):this._ts?ti(r.rawTime(i),this):this._tTime:this._tTime},t.revert=function(i){i===void 0&&(i=Mn);var r=rt;return rt=i,dr(this)&&(this.timeline&&this.timeline.revert(i),this.totalTime(-.01,i.suppressEvents)),this.data!=="nested"&&i.kill!==!1&&this.kill(),rt=r,this},t.globalTime=function(i){for(var r=this,a=arguments.length?i:r.rawTime();r;)a=r._start+a/(Math.abs(r._ts)||1),r=r._dp;return!this.parent&&this._sat?this._sat.globalTime(i):a},t.repeat=function(i){return arguments.length?(this._repeat=i===1/0?-2:i,Rr(this)):this._repeat===-2?1/0:this._repeat},t.repeatDelay=function(i){if(arguments.length){var r=this._time;return this._rDelay=i,Rr(this),r?this.time(r):this}return this._rDelay},t.yoyo=function(i){return arguments.length?(this._yoyo=i,this):this._yoyo},t.seek=function(i,r){return this.totalTime(kt(this,i),ft(r))},t.restart=function(i,r){return this.play().totalTime(i?-this._delay:0,ft(r)),this._dur||(this._zTime=-B),this},t.play=function(i,r){return i!=null&&this.seek(i,r),this.reversed(!1).paused(!1)},t.reverse=function(i,r){return i!=null&&this.seek(i||this.totalDuration(),r),this.reversed(!0).paused(!1)},t.pause=function(i,r){return i!=null&&this.seek(i,r),this.paused(!0)},t.resume=function(){return this.paused(!1)},t.reversed=function(i){return arguments.length?(!!i!==this.reversed()&&this.timeScale(-this._rts||(i?-B:0)),this):this._rts<0},t.invalidate=function(){return this._initted=this._act=0,this._zTime=-B,this},t.isActive=function(){var i=this.parent||this._dp,r=this._start,a;return!!(!i||this._ts&&this._initted&&i.isActive()&&(a=i.rawTime(!0))>=r&&a<this.endTime(!0)-B)},t.eventCallback=function(i,r,a){var s=this.vars;return arguments.length>1?(r?(s[i]=r,a&&(s[i+"Params"]=a),i==="onUpdate"&&(this._onUpdate=r)):delete s[i],this):s[i]},t.then=function(i){var r=this,a=r._prom;return new Promise(function(s){var o=J(i)?i:ba,l=function(){var d=r.then;r.then=null,a&&a(),J(o)&&(o=o(r))&&(o.then||o===r)&&(r.then=d),s(o),r.then=d};r._initted&&r.totalProgress()===1&&r._ts>=0||!r._tTime&&r._ts<0?l():r._prom=l})},t.kill=function(){Se(this)},n})();St(Re.prototype,{_time:0,_start:0,_end:0,_tTime:0,_tDur:0,_dirty:0,_repeat:0,_yoyo:!1,parent:null,_initted:!1,_rDelay:0,_ts:1,_dp:0,ratio:0,_zTime:-B,_prom:0,_ps:!1,_rts:1});var ut=(function(n){oa(t,n);function t(i,r){var a;return i===void 0&&(i={}),a=n.call(this,i)||this,a.labels={},a.smoothChildTiming=!!i.smoothChildTiming,a.autoRemoveChildren=!!i.autoRemoveChildren,a._sort=ft(i.sortChildren),W&&It(i.parent||W,zt(a),r),i.reversed&&a.reverse(),i.paused&&a.paused(!0),i.scrollTrigger&&$a(zt(a),i.scrollTrigger),a}var e=t.prototype;return e.to=function(r,a,s){return Te(0,arguments,this),this},e.from=function(r,a,s){return Te(1,arguments,this),this},e.fromTo=function(r,a,s,o){return Te(2,arguments,this),this},e.set=function(r,a,s){return a.duration=0,a.parent=this,Ee(a).repeatDelay||(a.repeat=0),a.immediateRender=!!a.immediateRender,new tt(r,a,kt(this,s),1),this},e.call=function(r,a,s){return It(this,tt.delayedCall(0,r,a),s)},e.staggerTo=function(r,a,s,o,l,c,d){return s.duration=a,s.stagger=s.stagger||o,s.onComplete=c,s.onCompleteParams=d,s.parent=this,new tt(r,s,kt(this,l)),this},e.staggerFrom=function(r,a,s,o,l,c,d){return s.runBackwards=1,Ee(s).immediateRender=ft(s.immediateRender),this.staggerTo(r,a,s,o,l,c,d)},e.staggerFromTo=function(r,a,s,o,l,c,d,f){return o.startAt=s,Ee(o).immediateRender=ft(o.immediateRender),this.staggerTo(r,a,o,l,c,d,f)},e.render=function(r,a,s){var o=this._time,l=this._dirty?this.totalDuration():this._tDur,c=this._dur,d=r<=0?0:X(r),f=this._zTime<0!=r<0&&(this._initted||!c),p,m,h,u,v,y,b,k,T,w,D,A;if(this!==W&&d>l&&r>=0&&(d=l),d!==this._tTime||s||f){if(o!==this._time&&c&&(d+=this._time-o,r+=this._time-o),p=d,T=this._start,k=this._ts,y=!k,f&&(c||(o=this._zTime),(r||!a)&&(this._zTime=r)),this._repeat){if(D=this._yoyo,v=c+this._rDelay,this._repeat<-1&&r<0)return this.totalTime(v*100+r,a,s);if(p=X(d%v),d===l?(u=this._repeat,p=c):(w=X(d/v),u=~~w,u&&u===w&&(p=c,u--),p>c&&(p=c)),w=_e(this._tTime,v),!o&&this._tTime&&w!==u&&this._tTime-w*v-this._dur<=0&&(w=u),D&&u&1&&(p=c-p,A=1),u!==w&&!this._lock){var O=D&&w&1,_=O===(D&&u&1);if(u<w&&(O=!O),o=O?0:d%c?c:d,this._lock=1,this.render(o||(A?0:X(u*v)),a,!c)._lock=0,this._tTime=d,!a&&this.parent&&yt(this,"onRepeat"),this.vars.repeatRefresh&&!A&&(this.invalidate()._lock=1,w=u),o&&o!==this._time||y!==!this._ts||this.vars.onRepeat&&!this.parent&&!this._act)return this;if(c=this._dur,l=this._tDur,_&&(this._lock=2,o=O?c:-1e-4,this.render(o,!0),this.vars.repeatRefresh&&!A&&this.invalidate()),this._lock=0,!this._ts&&!y)return this}}if(this._hasPause&&!this._forcing&&this._lock<2&&(b=Un(this,X(o),X(p)),b&&(d-=p-(p=b._start))),this._tTime=d,this._time=p,this._act=!!k,this._initted||(this._onUpdate=this.vars.onUpdate,this._initted=1,this._zTime=r,o=0),!o&&d&&c&&!a&&!w&&(yt(this,"onStart"),this._tTime!==d))return this;if(p>=o&&r>=0)for(m=this._first;m;){if(h=m._next,(m._act||p>=m._start)&&m._ts&&b!==m){if(m.parent!==this)return this.render(r,a,s);if(m.render(m._ts>0?(p-m._start)*m._ts:(m._dirty?m.totalDuration():m._tDur)+(p-m._start)*m._ts,a,s),p!==this._time||!this._ts&&!y){b=0,h&&(d+=this._zTime=-B);break}}m=h}else{m=this._last;for(var x=r<0?r:p;m;){if(h=m._prev,(m._act||x<=m._end)&&m._ts&&b!==m){if(m.parent!==this)return this.render(r,a,s);if(m.render(m._ts>0?(x-m._start)*m._ts:(m._dirty?m.totalDuration():m._tDur)+(x-m._start)*m._ts,a,s||rt&&dr(m)),p!==this._time||!this._ts&&!y){b=0,h&&(d+=this._zTime=x?-B:B);break}}m=h}}if(b&&!a&&(this.pause(),b.render(p>=o?0:-B)._zTime=p>=o?1:-1,this._ts))return this._start=T,oi(this),this.render(r,a,s);this._onUpdate&&!a&&yt(this,"onUpdate",!0),(d===l&&this._tTime>=this.totalDuration()||!d&&o)&&(T===this._start||Math.abs(k)!==Math.abs(this._ts))&&(this._lock||((r||!c)&&(d===l&&this._ts>0||!d&&this._ts<0)&&Ht(this,1),!a&&!(r<0&&!o)&&(d||o||!l)&&(yt(this,d===l&&r>=0?"onComplete":"onReverseComplete",!0),this._prom&&!(d<l&&this.timeScale()>0)&&this._prom())))}return this},e.add=function(r,a){var s=this;if(Ft(a)||(a=kt(this,a,r)),!(r instanceof Re)){if(ot(r))return r.forEach(function(o){return s.add(o,a)}),this;if(et(r))return this.addLabel(r,a);if(J(r))r=tt.delayedCall(0,r);else return this}return this!==r?It(this,r,a):this},e.getChildren=function(r,a,s,o){r===void 0&&(r=!0),a===void 0&&(a=!0),s===void 0&&(s=!0),o===void 0&&(o=-Tt);for(var l=[],c=this._first;c;)c._start>=o&&(c instanceof tt?a&&l.push(c):(s&&l.push(c),r&&l.push.apply(l,c.getChildren(!0,a,s)))),c=c._next;return l},e.getById=function(r){for(var a=this.getChildren(1,1,1),s=a.length;s--;)if(a[s].vars.id===r)return a[s]},e.remove=function(r){return et(r)?this.removeLabel(r):J(r)?this.killTweensOf(r):(r.parent===this&&si(this,r),r===this._recent&&(this._recent=this._last),se(this))},e.totalTime=function(r,a){return arguments.length?(this._forcing=1,!this._dp&&this._ts&&(this._start=X(bt.time-(this._ts>0?r/this._ts:(this.totalDuration()-r)/-this._ts))),n.prototype.totalTime.call(this,r,a),this._forcing=0,this):this._tTime},e.addLabel=function(r,a){return this.labels[r]=kt(this,a),this},e.removeLabel=function(r){return delete this.labels[r],this},e.addPause=function(r,a,s){var o=tt.delayedCall(0,a||De,s);return o.data="isPause",this._hasPause=1,It(this,o,kt(this,r))},e.removePause=function(r){var a=this._first;for(r=kt(this,r);a;)a._start===r&&a.data==="isPause"&&Ht(a),a=a._next},e.killTweensOf=function(r,a,s){for(var o=this.getTweensOf(r,s),l=o.length;l--;)Ut!==o[l]&&o[l].kill(r,a);return this},e.getTweensOf=function(r,a){for(var s=[],o=At(r),l=this._first,c=Ft(a),d;l;)l instanceof tt?Nn(l._targets,o)&&(c?(!Ut||l._initted&&l._ts)&&l.globalTime(0)<=a&&l.globalTime(l.totalDuration())>a:!a||l.isActive())&&s.push(l):(d=l.getTweensOf(o,a)).length&&s.push.apply(s,d),l=l._next;return s},e.tweenTo=function(r,a){a=a||{};var s=this,o=kt(s,r),l=a,c=l.startAt,d=l.onStart,f=l.onStartParams,p=l.immediateRender,m,h=tt.to(s,St({ease:a.ease||"none",lazy:!1,immediateRender:!1,time:o,overwrite:"auto",duration:a.duration||Math.abs((o-(c&&"time"in c?c.time:s._time))/s.timeScale())||B,onStart:function(){if(s.pause(),!m){var v=a.duration||Math.abs((o-(c&&"time"in c?c.time:s._time))/s.timeScale());h._dur!==v&&be(h,v,0,1).render(h._time,!0,!0),m=1}d&&d.apply(h,f||[])}},a));return p?h.render(0):h},e.tweenFromTo=function(r,a,s){return this.tweenTo(a,St({startAt:{time:kt(this,r)}},s))},e.recent=function(){return this._recent},e.nextLabel=function(r){return r===void 0&&(r=this._time),Mr(this,kt(this,r))},e.previousLabel=function(r){return r===void 0&&(r=this._time),Mr(this,kt(this,r),1)},e.currentLabel=function(r){return arguments.length?this.seek(r,!0):this.previousLabel(this._time+B)},e.shiftChildren=function(r,a,s){s===void 0&&(s=0);var o=this._first,l=this.labels,c;for(r=X(r);o;)o._start>=s&&(o._start+=r,o._end+=r),o=o._next;if(a)for(c in l)l[c]>=s&&(l[c]+=r);return se(this)},e.invalidate=function(r){var a=this._first;for(this._lock=0;a;)a.invalidate(r),a=a._next;return n.prototype.invalidate.call(this,r)},e.clear=function(r){r===void 0&&(r=!0);for(var a=this._first,s;a;)s=a._next,this.remove(a),a=s;return this._dp&&(this._time=this._tTime=this._pTime=0),r&&(this.labels={}),se(this)},e.totalDuration=function(r){var a=0,s=this,o=s._last,l=Tt,c,d,f;if(arguments.length)return s.timeScale((s._repeat<0?s.duration():s.totalDuration())/(s.reversed()?-r:r));if(s._dirty){for(f=s.parent;o;)c=o._prev,o._dirty&&o.totalDuration(),d=o._start,d>l&&s._sort&&o._ts&&!s._lock?(s._lock=1,It(s,o,d-o._delay,1)._lock=0):l=d,d<0&&o._ts&&(a-=d,(!f&&!s._dp||f&&f.smoothChildTiming)&&(s._start+=X(d/s._ts),s._time-=d,s._tTime-=d),s.shiftChildren(-d,!1,-1/0),l=0),o._end>a&&o._ts&&(a=o._end),o=c;be(s,s===W&&s._time>a?s._time:a,1,1),s._dirty=0}return s._tDur},t.updateRoot=function(r){if(W._ts&&(va(W,ti(r,W)),ha=bt.frame),bt.frame>=Dr){Dr+=$t.autoSleep||120;var a=W._first;if((!a||!a._ts)&&$t.autoSleep&&bt._listeners.length<2){for(;a&&!a._ts;)a=a._next;a||bt.sleep()}}},t})(Re);St(ut.prototype,{_lock:0,_hasPause:0,_forcing:0});var os=function(t,e,i,r,a,s,o){var l=new mt(this._pt,t,e,0,1,Va,null,a),c=0,d=0,f,p,m,h,u,v,y,b;for(l.b=i,l.e=r,i+="",r+="",(y=~r.indexOf("random("))&&(r=Le(r)),s&&(b=[i,r],s(b,t,e),i=b[0],r=b[1]),p=i.match(gi)||[];f=gi.exec(r);)h=f[0],u=r.substring(c,f.index),m?m=(m+1)%5:u.substr(-5)==="rgba("&&(m=1),h!==p[d++]&&(v=parseFloat(p[d-1])||0,l._pt={_next:l._pt,p:u||d===1?u:",",s:v,c:h.charAt(1)==="="?pe(v,h)-v:parseFloat(h)-v,m:m&&m<4?Math.round:0},c=gi.lastIndex);return l.c=c<r.length?r.substring(c,r.length):"",l.fp=o,(ua.test(r)||y)&&(l.e=0),this._pt=l,l},ur=function(t,e,i,r,a,s,o,l,c,d){J(r)&&(r=r(a||0,t,s));var f=t[e],p=i!=="get"?i:J(f)?c?t[e.indexOf("set")||!J(t["get"+e.substr(3)])?e:"get"+e.substr(3)](c):t[e]():f,m=J(f)?c?fs:Fa:pr,h;if(et(r)&&(~r.indexOf("random(")&&(r=Le(r)),r.charAt(1)==="="&&(h=pe(p,r)+(st(p)||0),(h||h===0)&&(r=h))),!d||p!==r||Ni)return!isNaN(p*r)&&r!==""?(h=new mt(this._pt,t,e,+p||0,r-(p||0),typeof f=="boolean"?ms:Ba,0,m),c&&(h.fp=c),o&&h.modifier(o,this,t),this._pt=h):(!f&&!(e in t)&&sr(e,r),os.call(this,t,e,p,r,m,l||$t.stringFilter,c))},ls=function(t,e,i,r,a){if(J(t)&&(t=Ae(t,a,e,i,r)),!qt(t)||t.style&&t.nodeType||ot(t)||ca(t))return et(t)?Ae(t,a,e,i,r):t;var s={},o;for(o in t)s[o]=Ae(t[o],a,e,i,r);return s},qa=function(t,e,i,r,a,s){var o,l,c,d;if(_t[t]&&(o=new _t[t]).init(a,o.rawVars?e[t]:ls(e[t],r,a,s,i),i,r,s)!==!1&&(i._pt=l=new mt(i._pt,a,t,0,1,o.render,o,0,o.priority),i!==fe))for(c=i._ptLookup[i._targets.indexOf(a)],d=o._props.length;d--;)c[o._props[d]]=l;return o},Ut,Ni,fr=function n(t,e,i){var r=t.vars,a=r.ease,s=r.startAt,o=r.immediateRender,l=r.lazy,c=r.onUpdate,d=r.runBackwards,f=r.yoyoEase,p=r.keyframes,m=r.autoRevert,h=t._dur,u=t._startAt,v=t._targets,y=t.parent,b=y&&y.data==="nested"?y.vars.targets:v,k=t._overwrite==="auto"&&!ir,T=t.timeline,w=r.easeReverse||f,D,A,O,_,x,C,S,L,g,$,I,P,M;if(T&&(!p||!a)&&(a="none"),t._ease=oe(a,Oe.ease),t._rEase=w&&(oe(w)||t._ease),t._from=!T&&!!r.runBackwards,t._from&&(t.ratio=1),!T||p&&!r.stagger){if(L=v[0]?ne(v[0]).harness:0,P=L&&r[L.prop],D=Ze(r,or),u&&(u._zTime<0&&u.progress(1),e<0&&d&&o&&!m?u.render(-1,!0):u.revert(d&&h?Xe:Rn),u._lazy=0),s){if(Ht(t._startAt=tt.set(v,St({data:"isStart",overwrite:!1,parent:y,immediateRender:!0,lazy:!u&&ft(l),startAt:null,delay:0,onUpdate:c&&function(){return yt(t,"onUpdate")},stagger:0},s))),t._startAt._dp=0,t._startAt._sat=t,e<0&&(rt||!o&&!m)&&t._startAt.revert(Xe),o&&h&&e<=0&&i<=0){e&&(t._zTime=e);return}}else if(d&&h&&!u){if(e&&(o=!1),O=St({overwrite:!1,data:"isFromStart",lazy:o&&!u&&ft(l),immediateRender:o,stagger:0,parent:y},D),P&&(O[L.prop]=P),Ht(t._startAt=tt.set(v,O)),t._startAt._dp=0,t._startAt._sat=t,e<0&&(rt?t._startAt.revert(Xe):t._startAt.render(-1,!0)),t._zTime=e,!o)n(t._startAt,B,B);else if(!e)return}for(t._pt=t._ptCache=0,l=h&&ft(l)||l&&!h,A=0;A<v.length;A++){if(x=v[A],S=x._gsap||cr(v)[A]._gsap,t._ptLookup[A]=$={},Pi[S.id]&&Xt.length&&Qe(),I=b===v?A:b.indexOf(x),L&&(g=new L).init(x,P||D,t,I,b)!==!1&&(t._pt=_=new mt(t._pt,x,g.name,0,1,g.render,g,0,g.priority),g._props.forEach(function(U){$[U]=_}),g.priority&&(C=1)),!L||P)for(O in D)_t[O]&&(g=qa(O,D,t,I,x,b))?g.priority&&(C=1):$[O]=_=ur.call(t,x,O,"get",D[O],I,b,0,r.stringFilter);t._op&&t._op[A]&&t.kill(x,t._op[A]),k&&t._pt&&(Ut=t,W.killTweensOf(x,$,t.globalTime(e)),M=!t.parent,Ut=0),t._pt&&l&&(Pi[S.id]=1)}C&&Ua(t),t._onInit&&t._onInit(t)}t._onUpdate=c,t._initted=(!t._op||t._pt)&&!M,p&&e<=0&&T.render(Tt,!0,!0)},cs=function(t,e,i,r,a,s,o,l){var c=(t._pt&&t._ptCache||(t._ptCache={}))[e],d,f,p,m;if(!c)for(c=t._ptCache[e]=[],p=t._ptLookup,m=t._targets.length;m--;){if(d=p[m][e],d&&d.d&&d.d._pt)for(d=d.d._pt;d&&d.p!==e&&d.fp!==e;)d=d._next;if(!d)return Ni=1,t.vars[e]="+=0",fr(t,o),Ni=0,l?Pe(e+" not eligible for reset. Try splitting into individual properties"):1;c.push(d)}for(m=c.length;m--;)f=c[m],d=f._pt||f,d.s=(r||r===0)&&!a?r:d.s+(r||0)+s*d.c,d.c=i-d.s,f.e&&(f.e=Q(i)+st(f.e)),f.b&&(f.b=d.s+st(f.b))},ds=function(t,e){var i=t[0]?ne(t[0]).harness:0,r=i&&i.aliases,a,s,o,l;if(!r)return e;a=ve({},e);for(s in r)if(s in a)for(l=r[s].split(","),o=l.length;o--;)a[l[o]]=a[s];return a},us=function(t,e,i,r){var a=e.ease||r||"power1.inOut",s,o;if(ot(e))o=i[t]||(i[t]=[]),e.forEach(function(l,c){return o.push({t:c/(e.length-1)*100,v:l,e:a})});else for(s in e)o=i[s]||(i[s]=[]),s==="ease"||o.push({t:parseFloat(t),v:e[s],e:a})},Ae=function(t,e,i,r,a){return J(t)?t.call(e,i,r,a):et(t)&&~t.indexOf("random(")?Le(t):t},za=lr+"repeat,repeatDelay,yoyo,repeatRefresh,yoyoEase,easeReverse,autoRevert",ja={};pt(za+",id,stagger,delay,duration,paused,scrollTrigger",function(n){return ja[n]=1});var tt=(function(n){oa(t,n);function t(i,r,a,s){var o;typeof r=="number"&&(a.duration=r,r=a,a=null),o=n.call(this,s?r:Ee(r))||this;var l=o.vars,c=l.duration,d=l.delay,f=l.immediateRender,p=l.stagger,m=l.overwrite,h=l.keyframes,u=l.defaults,v=l.scrollTrigger,y=r.parent||W,b=(ot(i)||ca(i)?Ft(i[0]):"length"in r)?[i]:At(i),k,T,w,D,A,O,_,x;if(o._targets=b.length?cr(b):Pe("GSAP target "+i+" not found. https://gsap.com",!$t.nullTargetWarn)||[],o._ptLookup=[],o._overwrite=m,h||p||Ye(c)||Ye(d)){r=o.vars;var C=r.easeReverse||r.yoyoEase;if(k=o.timeline=new ut({data:"nested",defaults:u||{},targets:y&&y.data==="nested"?y.vars.targets:b}),k.kill(),k.parent=k._dp=zt(o),k._start=0,p||Ye(c)||Ye(d)){if(D=b.length,_=p&&Ea(p),qt(p))for(A in p)~za.indexOf(A)&&(x||(x={}),x[A]=p[A]);for(T=0;T<D;T++)w=Ze(r,ja),w.stagger=0,C&&(w.easeReverse=C),x&&ve(w,x),O=b[T],w.duration=+Ae(c,zt(o),T,O,b),w.delay=(+Ae(d,zt(o),T,O,b)||0)-o._delay,!p&&D===1&&w.delay&&(o._delay=d=w.delay,o._start+=d,w.delay=0),k.to(O,w,_?_(T,O,b):0),k._ease=j.none;k.duration()?c=d=0:o.timeline=0}else if(h){Ee(St(k.vars.defaults,{ease:"none"})),k._ease=oe(h.ease||r.ease||"none");var S=0,L,g,$;if(ot(h))h.forEach(function(I){return k.to(b,I,">")}),k.duration();else{w={};for(A in h)A==="ease"||A==="easeEach"||us(A,h[A],w,h.easeEach);for(A in w)for(L=w[A].sort(function(I,P){return I.t-P.t}),S=0,T=0;T<L.length;T++)g=L[T],$={ease:g.e,duration:(g.t-(T?L[T-1].t:0))/100*c},$[A]=g.v,k.to(b,$,S),S+=$.duration;k.duration()<c&&k.to({},{duration:c-k.duration()})}}c||o.duration(c=k.duration())}else o.timeline=0;return m===!0&&!ir&&(Ut=zt(o),W.killTweensOf(b),Ut=0),It(y,zt(o),a),r.reversed&&o.reverse(),r.paused&&o.paused(!0),(f||!c&&!h&&o._start===X(y._time)&&ft(f)&&Fn(zt(o))&&y.data!=="nested")&&(o._tTime=-B,o.render(Math.max(0,-d)||0)),v&&$a(zt(o),v),o}var e=t.prototype;return e.render=function(r,a,s){var o=this._time,l=this._tDur,c=this._dur,d=r<0,f=r>l-B&&!d?l:r<B?0:r,p,m,h,u,v,y,b,k;if(!c)Vn(this,r,a,s);else if(f!==this._tTime||!r||s||!this._initted&&this._tTime||this._startAt&&this._zTime<0!==d||this._lazy){if(p=f,k=this.timeline,this._repeat){if(u=c+this._rDelay,this._repeat<-1&&d)return this.totalTime(u*100+r,a,s);if(p=X(f%u),f===l?(h=this._repeat,p=c):(v=X(f/u),h=~~v,h&&h===v?(p=c,h--):p>c&&(p=c)),y=this._yoyo&&h&1,y&&(p=c-p),v=_e(this._tTime,u),p===o&&!s&&this._initted&&h===v)return this._tTime=f,this;h!==v&&this.vars.repeatRefresh&&!y&&!this._lock&&p!==u&&this._initted&&(this._lock=s=1,this.render(X(u*h),!0).invalidate()._lock=0)}if(!this._initted){if(wa(this,d?r:p,s,a,f))return this._tTime=0,this;if(o!==this._time&&!(s&&this.vars.repeatRefresh&&h!==v))return this;if(c!==this._dur)return this.render(r,a,s)}if(this._rEase){var T=p<o;if(T!==this._inv){var w=T?o:c-o;this._inv=T,this._from&&(this.ratio=1-this.ratio),this._invRatio=this.ratio,this._invTime=o,this._invRecip=w?(T?-1:1)/w:0,this._invScale=T?-this.ratio:1-this.ratio,this._invEase=T?this._rEase:this._ease}this.ratio=b=this._invRatio+this._invScale*this._invEase((p-this._invTime)*this._invRecip)}else this.ratio=b=this._ease(p/c);if(this._from&&(this.ratio=b=1-b),this._tTime=f,this._time=p,!this._act&&this._ts&&(this._act=1,this._lazy=0),!o&&f&&!a&&!v&&(yt(this,"onStart"),this._tTime!==f))return this;for(m=this._pt;m;)m.r(b,m.d),m=m._next;k&&k.render(r<0?r:k._dur*k._ease(p/this._dur),a,s)||this._startAt&&(this._zTime=r),this._onUpdate&&!a&&(d&&Di(this,r,a,s),yt(this,"onUpdate")),this._repeat&&h!==v&&this.vars.onRepeat&&!a&&this.parent&&yt(this,"onRepeat"),(f===this._tDur||!f)&&this._tTime===f&&(d&&!this._onUpdate&&Di(this,r,!0,!0),(r||!c)&&(f===this._tDur&&this._ts>0||!f&&this._ts<0)&&Ht(this,1),!a&&!(d&&!o)&&(f||o||y)&&(yt(this,f===l?"onComplete":"onReverseComplete",!0),this._prom&&!(f<l&&this.timeScale()>0)&&this._prom()))}return this},e.targets=function(){return this._targets},e.invalidate=function(r){return(!r||!this.vars.runBackwards)&&(this._startAt=0),this._pt=this._op=this._onUpdate=this._lazy=this.ratio=0,this._ptLookup=[],this.timeline&&this.timeline.invalidate(r),n.prototype.invalidate.call(this,r)},e.resetTo=function(r,a,s,o,l){Ie||bt.wake(),this._ts||this.play();var c=Math.min(this._dur,(this._dp._time-this._start)*this._ts),d;return this._initted||fr(this,c),d=this._ease(c/this._dur),cs(this,r,a,s,o,d,c,l)?this.resetTo(r,a,s,o,1):(li(this,0),this.parent||ya(this._dp,this,"_first","_last",this._dp._sort?"_start":0),this.render(0))},e.kill=function(r,a){if(a===void 0&&(a="all"),!r&&(!a||a==="all"))return this._lazy=this._pt=0,this.parent?Se(this):this.scrollTrigger&&this.scrollTrigger.kill(!!rt),this;if(this.timeline){var s=this.timeline.totalDuration();return this.timeline.killTweensOf(r,a,Ut&&Ut.vars.overwrite!==!0)._first||Se(this),this.parent&&s!==this.timeline.totalDuration()&&be(this,this._dur*this.timeline._tDur/s,0,1),this}var o=this._targets,l=r?At(r):o,c=this._ptLookup,d=this._pt,f,p,m,h,u,v,y;if((!a||a==="all")&&zn(o,l))return a==="all"&&(this._pt=0),Se(this);for(f=this._op=this._op||[],a!=="all"&&(et(a)&&(u={},pt(a,function(b){return u[b]=1}),a=u),a=ds(o,a)),y=o.length;y--;)if(~l.indexOf(o[y])){p=c[y],a==="all"?(f[y]=a,h=p,m={}):(m=f[y]=f[y]||{},h=a);for(u in h)v=p&&p[u],v&&((!("kill"in v.d)||v.d.kill(u)===!0)&&si(this,v,"_pt"),delete p[u]),m!=="all"&&(m[u]=1)}return this._initted&&!this._pt&&d&&Se(this),this},t.to=function(r,a){return new t(r,a,arguments[2])},t.from=function(r,a){return Te(1,arguments)},t.delayedCall=function(r,a,s,o){return new t(a,0,{immediateRender:!1,lazy:!1,overwrite:!1,delay:r,onComplete:a,onReverseComplete:a,onCompleteParams:s,onReverseCompleteParams:s,callbackScope:o})},t.fromTo=function(r,a,s){return Te(2,arguments)},t.set=function(r,a){return a.duration=0,a.repeatDelay||(a.repeat=0),new t(r,a)},t.killTweensOf=function(r,a,s){return W.killTweensOf(r,a,s)},t})(Re);St(tt.prototype,{_targets:[],_lazy:0,_startAt:0,_op:0,_onInit:0});pt("staggerTo,staggerFrom,staggerFromTo",function(n){tt[n]=function(){var t=new ut,e=Ii.call(arguments,0);return e.splice(n==="staggerFromTo"?5:4,0,0),t[n].apply(t,e)}});var pr=function(t,e,i){return t[e]=i},Fa=function(t,e,i){return t[e](i)},fs=function(t,e,i,r){return t[e](r.fp,i)},ps=function(t,e,i){return t.setAttribute(e,i)},mr=function(t,e){return J(t[e])?Fa:rr(t[e])&&t.setAttribute?ps:pr},Ba=function(t,e){return e.set(e.t,e.p,Math.round((e.s+e.c*t)*1e6)/1e6,e)},ms=function(t,e){return e.set(e.t,e.p,!!(e.s+e.c*t),e)},Va=function(t,e){var i=e._pt,r="";if(!t&&e.b)r=e.b;else if(t===1&&e.e)r=e.e;else{for(;i;)r=i.p+(i.m?i.m(i.s+i.c*t):Math.round((i.s+i.c*t)*1e4)/1e4)+r,i=i._next;r+=e.c}e.set(e.t,e.p,r,e)},hr=function(t,e){for(var i=e._pt;i;)i.r(t,i.d),i=i._next},hs=function(t,e,i,r){for(var a=this._pt,s;a;)s=a._next,a.p===r&&a.modifier(t,e,i),a=s},gs=function(t){for(var e=this._pt,i,r;e;)r=e._next,e.p===t&&!e.op||e.op===t?si(this,e,"_pt"):e.dep||(i=1),e=r;return!i},vs=function(t,e,i,r){r.mSet(t,e,r.m.call(r.tween,i,r.mt),r)},Ua=function(t){for(var e=t._pt,i,r,a,s;e;){for(i=e._next,r=a;r&&r.pr>e.pr;)r=r._next;(e._prev=r?r._prev:s)?e._prev._next=e:a=e,(e._next=r)?r._prev=e:s=e,e=i}t._pt=a},mt=(function(){function n(e,i,r,a,s,o,l,c,d){this.t=i,this.s=a,this.c=s,this.p=r,this.r=o||Ba,this.d=l||this,this.set=c||pr,this.pr=d||0,this._next=e,e&&(e._prev=this)}var t=n.prototype;return t.modifier=function(i,r,a){this.mSet=this.mSet||this.set,this.set=vs,this.m=i,this.mt=a,this.tween=r},n})();pt(lr+"parent,duration,ease,delay,overwrite,runBackwards,startAt,yoyo,immediateRender,repeat,repeatDelay,data,paused,reversed,lazy,callbackScope,stringFilter,id,yoyoEase,stagger,inherit,repeatRefresh,keyframes,autoRevert,scrollTrigger,easeReverse",function(n){return or[n]=1});wt.TweenMax=wt.TweenLite=tt;wt.TimelineLite=wt.TimelineMax=ut;W=new ut({sortChildren:!1,defaults:Oe,autoRemoveChildren:!0,id:"root",smoothChildTiming:!0});$t.stringFilter=Ra;var le=[],He={},_s=[],qr=0,bs=0,xi=function(t){return(He[t]||_s).map(function(e){return e()})},qi=function(){var t=Date.now(),e=[];t-qr>2&&(xi("matchMediaInit"),le.forEach(function(i){var r=i.queries,a=i.conditions,s,o,l,c;for(o in r)s=Lt.matchMedia(r[o]).matches,s&&(l=1),s!==a[o]&&(a[o]=s,c=1);c&&(i.revert(),l&&e.push(i))}),xi("matchMediaRevert"),e.forEach(function(i){return i.onMatch(i,function(r){return i.add(null,r)})}),qr=t,xi("matchMedia"))},Ya=(function(){function n(e,i){this.selector=i&&Ri(i),this.data=[],this._r=[],this.isReverted=!1,this.id=bs++,e&&this.add(e)}var t=n.prototype;return t.add=function(i,r,a){J(i)&&(a=r,r=i,i=J);var s=this,o=function(){var c=Y,d=s.selector,f;return c&&c!==s&&c.data.push(s),a&&(s.selector=Ri(a)),Y=s,f=r.apply(s,arguments),J(f)&&s._r.push(f),Y=c,s.selector=d,s.isReverted=!1,f};return s.last=o,i===J?o(s,function(l){return s.add(null,l)}):i?s[i]=o:o},t.ignore=function(i){var r=Y;Y=null,i(this),Y=r},t.getTweens=function(){var i=[];return this.data.forEach(function(r){return r instanceof n?i.push.apply(i,r.getTweens()):r instanceof tt&&!(r.parent&&r.parent.data==="nested")&&i.push(r)}),i},t.clear=function(){this._r.length=this.data.length=0},t.kill=function(i,r){var a=this;if(i?(function(){for(var o=a.getTweens(),l=a.data.length,c;l--;)c=a.data[l],c.data==="isFlip"&&(c.revert(),c.getChildren(!0,!0,!1).forEach(function(d){return o.splice(o.indexOf(d),1)}));for(o.map(function(d){return{g:d._dur||d._delay||d._sat&&!d._sat.vars.immediateRender?d.globalTime(0):-1/0,t:d}}).sort(function(d,f){return f.g-d.g||-1/0}).forEach(function(d){return d.t.revert(i)}),l=a.data.length;l--;)c=a.data[l],c instanceof ut?c.data!=="nested"&&(c.scrollTrigger&&c.scrollTrigger.revert(),c.kill()):!(c instanceof tt)&&c.revert&&c.revert(i);a._r.forEach(function(d){return d(i,a)}),a.isReverted=!0})():this.data.forEach(function(o){return o.kill&&o.kill()}),this.clear(),r)for(var s=le.length;s--;)le[s].id===this.id&&le.splice(s,1)},t.revert=function(i){this.kill(i||{})},n})(),ys=(function(){function n(e){this.contexts=[],this.scope=e,Y&&Y.data.push(this)}var t=n.prototype;return t.add=function(i,r,a){qt(i)||(i={matches:i});var s=new Ya(0,a||this.scope),o=s.conditions={},l,c,d;Y&&!s.selector&&(s.selector=Y.selector),this.contexts.push(s),r=s.add("onMatch",r),s.queries=i;for(c in i)c==="all"?d=1:(l=Lt.matchMedia(i[c]),l&&(le.indexOf(s)<0&&le.push(s),(o[c]=l.matches)&&(d=1),l.addListener?l.addListener(qi):l.addEventListener("change",qi)));return d&&r(s,function(f){return s.add(null,f)}),this},t.revert=function(i){this.kill(i||{})},t.kill=function(i){this.contexts.forEach(function(r){return r.kill(i,!0)})},n})(),ei={registerPlugin:function(){for(var t=arguments.length,e=new Array(t),i=0;i<t;i++)e[i]=arguments[i];e.forEach(function(r){return Da(r)})},timeline:function(t){return new ut(t)},getTweensOf:function(t,e){return W.getTweensOf(t,e)},getProperty:function(t,e,i,r){et(t)&&(t=At(t)[0]);var a=ne(t||{}).get,s=i?ba:_a;return i==="native"&&(i=""),t&&(e?s((_t[e]&&_t[e].get||a)(t,e,i,r)):function(o,l,c){return s((_t[o]&&_t[o].get||a)(t,o,l,c))})},quickSetter:function(t,e,i){if(t=At(t),t.length>1){var r=t.map(function(d){return gt.quickSetter(d,e,i)}),a=r.length;return function(d){for(var f=a;f--;)r[f](d)}}t=t[0]||{};var s=_t[e],o=ne(t),l=o.harness&&(o.harness.aliases||{})[e]||e,c=s?function(d){var f=new s;fe._pt=0,f.init(t,i?d+i:d,fe,0,[t]),f.render(1,f),fe._pt&&hr(1,fe)}:o.set(t,l);return s?c:function(d){return c(t,l,i?d+i:d,o,1)}},quickTo:function(t,e,i){var r,a=gt.to(t,St((r={},r[e]="+=0.1",r.paused=!0,r.stagger=0,r),i||{})),s=function(l,c,d){return a.resetTo(e,l,c,d)};return s.tween=a,s},isTweening:function(t){return W.getTweensOf(t,!0).length>0},defaults:function(t){return t&&t.ease&&(t.ease=oe(t.ease,Oe.ease)),Lr(Oe,t||{})},config:function(t){return Lr($t,t||{})},registerEffect:function(t){var e=t.name,i=t.effect,r=t.plugins,a=t.defaults,s=t.extendTimeline;(r||"").split(",").forEach(function(o){return o&&!_t[o]&&!wt[o]&&Pe(e+" effect requires "+o+" plugin.")}),vi[e]=function(o,l,c){return i(At(o),St(l||{},a),c)},s&&(ut.prototype[e]=function(o,l,c){return this.add(vi[e](o,qt(l)?l:(c=l)&&{},this),c)})},registerEase:function(t,e){j[t]=oe(e)},parseEase:function(t,e){return arguments.length?oe(t,e):j},getById:function(t){return W.getById(t)},exportRoot:function(t,e){t===void 0&&(t={});var i=new ut(t),r,a;for(i.smoothChildTiming=ft(t.smoothChildTiming),W.remove(i),i._dp=0,i._time=i._tTime=W._time,r=W._first;r;)a=r._next,(e||!(!r._dur&&r instanceof tt&&r.vars.onComplete===r._targets[0]))&&It(i,r,r._start-r._delay),r=a;return It(W,i,0),i},context:function(t,e){return t?new Ya(t,e):Y},matchMedia:function(t){return new ys(t)},matchMediaRefresh:function(){return le.forEach(function(t){var e=t.conditions,i,r;for(r in e)e[r]&&(e[r]=!1,i=1);i&&t.revert()})||qi()},addEventListener:function(t,e){var i=He[t]||(He[t]=[]);~i.indexOf(e)||i.push(e)},removeEventListener:function(t,e){var i=He[t],r=i&&i.indexOf(e);r>=0&&i.splice(r,1)},utils:{wrap:Kn,wrapYoyo:Qn,distribute:Ea,random:Aa,snap:Ta,normalize:Jn,getUnit:st,clamp:Gn,splitColor:La,toArray:At,selector:Ri,mapRange:Oa,pipe:Wn,unitize:Hn,interpolate:Zn,shuffle:ka},install:pa,effects:vi,ticker:bt,updateRoot:ut.updateRoot,plugins:_t,globalTimeline:W,core:{PropTween:mt,globals:ma,Tween:tt,Timeline:ut,Animation:Re,getCache:ne,_removeLinkedListItem:si,reverting:function(){return rt},context:function(t){return t&&Y&&(Y.data.push(t),t._ctx=Y),Y},suppressOverwrites:function(t){return ir=t}}};pt("to,from,fromTo,delayedCall,set,killTweensOf",function(n){return ei[n]=tt[n]});bt.add(ut.updateRoot);fe=ei.to({},{duration:0});var xs=function(t,e){for(var i=t._pt;i&&i.p!==e&&i.op!==e&&i.fp!==e;)i=i._next;return i},$s=function(t,e){var i=t._targets,r,a,s;for(r in e)for(a=i.length;a--;)s=t._ptLookup[a][r],s&&(s=s.d)&&(s._pt&&(s=xs(s,r)),s&&s.modifier&&s.modifier(e[r],t,i[a],r))},$i=function(t,e){return{name:t,headless:1,rawVars:1,init:function(r,a,s){s._onInit=function(o){var l,c;if(et(a)&&(l={},pt(a,function(d){return l[d]=1}),a=l),e){l={};for(c in a)l[c]=e(a[c]);a=l}$s(o,a)}}}},gt=ei.registerPlugin({name:"attr",init:function(t,e,i,r,a){var s,o,l;this.tween=i;for(s in e)l=t.getAttribute(s)||"",o=this.add(t,"setAttribute",(l||0)+"",e[s],r,a,0,0,s),o.op=s,o.b=l,this._props.push(s)},render:function(t,e){for(var i=e._pt;i;)rt?i.set(i.t,i.p,i.b,i):i.r(t,i.d),i=i._next}},{name:"endArray",headless:1,init:function(t,e){for(var i=e.length;i--;)this.add(t,i,t[i]||0,e[i],0,0,0,0,0,1)}},$i("roundProps",Mi),$i("modifiers"),$i("snap",Ta))||ei;tt.version=ut.version=gt.version="3.15.0";fa=1;ar()&&ye();j.Power0;j.Power1;j.Power2;j.Power3;j.Power4;j.Linear;j.Quad;j.Cubic;j.Quart;j.Quint;j.Strong;j.Elastic;j.Back;j.SteppedEase;j.Bounce;j.Sine;j.Expo;j.Circ;/*!
 * CSSPlugin 3.15.0
 * https://gsap.com
 *
 * Copyright 2008-2026, GreenSock. All rights reserved.
 * Subject to the terms at https://gsap.com/standard-license
 * @author: Jack Doyle, jack@greensock.com
*/var zr,Yt,me,gr,ae,jr,vr,ws=function(){return typeof window<"u"},Bt={},re=180/Math.PI,he=Math.PI/180,de=Math.atan2,Fr=1e8,_r=/([A-Z])/g,Ss=/(left|right|width|margin|padding|x)/i,ks=/[\s,\(]\S/,Rt={autoAlpha:"opacity,visibility",scale:"scaleX,scaleY",alpha:"opacity"},zi=function(t,e){return e.set(e.t,e.p,Math.round((e.s+e.c*t)*1e4)/1e4+e.u,e)},Es=function(t,e){return e.set(e.t,e.p,t===1?e.e:Math.round((e.s+e.c*t)*1e4)/1e4+e.u,e)},Ts=function(t,e){return e.set(e.t,e.p,t?Math.round((e.s+e.c*t)*1e4)/1e4+e.u:e.b,e)},As=function(t,e){return e.set(e.t,e.p,t===1?e.e:t?Math.round((e.s+e.c*t)*1e4)/1e4+e.u:e.b,e)},Cs=function(t,e){var i=e.s+e.c*t;e.set(e.t,e.p,~~(i+(i<0?-.5:.5))+e.u,e)},Ga=function(t,e){return e.set(e.t,e.p,t?e.e:e.b,e)},Xa=function(t,e){return e.set(e.t,e.p,t!==1?e.b:e.e,e)},Os=function(t,e,i){return t.style[e]=i},Ps=function(t,e,i){return t.style.setProperty(e,i)},Ds=function(t,e,i){return t._gsap[e]=i},Ls=function(t,e,i){return t._gsap.scaleX=t._gsap.scaleY=i},Is=function(t,e,i,r,a){var s=t._gsap;s.scaleX=s.scaleY=i,s.renderTransform(a,s)},Rs=function(t,e,i,r,a){var s=t._gsap;s[e]=i,s.renderTransform(a,s)},H="transform",ht=H+"Origin",Ms=function n(t,e){var i=this,r=this.target,a=r.style,s=r._gsap;if(t in Bt&&a){if(this.tfm=this.tfm||{},t!=="transform")t=Rt[t]||t,~t.indexOf(",")?t.split(",").forEach(function(o){return i.tfm[o]=jt(r,o)}):this.tfm[t]=s.x?s[t]:jt(r,t),t===ht&&(this.tfm.zOrigin=s.zOrigin);else return Rt.transform.split(",").forEach(function(o){return n.call(i,o,e)});if(this.props.indexOf(H)>=0)return;s.svg&&(this.svgo=r.getAttribute("data-svg-origin"),this.props.push(ht,e,"")),t=H}(a||e)&&this.props.push(t,e,a[t])},Wa=function(t){t.translate&&(t.removeProperty("translate"),t.removeProperty("scale"),t.removeProperty("rotate"))},Ns=function(){var t=this.props,e=this.target,i=e.style,r=e._gsap,a,s;for(a=0;a<t.length;a+=3)t[a+1]?t[a+1]===2?e[t[a]](t[a+2]):e[t[a]]=t[a+2]:t[a+2]?i[t[a]]=t[a+2]:i.removeProperty(t[a].substr(0,2)==="--"?t[a]:t[a].replace(_r,"-$1").toLowerCase());if(this.tfm){for(s in this.tfm)r[s]=this.tfm[s];r.svg&&(r.renderTransform(),e.setAttribute("data-svg-origin",this.svgo||"")),a=vr(),(!a||!a.isStart)&&!i[H]&&(Wa(i),r.zOrigin&&i[ht]&&(i[ht]+=" "+r.zOrigin+"px",r.zOrigin=0,r.renderTransform()),r.uncache=1)}},Ha=function(t,e){var i={target:t,props:[],revert:Ns,save:Ms};return t._gsap||gt.core.getCache(t),e&&t.style&&t.nodeType&&e.split(",").forEach(function(r){return i.save(r)}),i},Ja,ji=function(t,e){var i=Yt.createElementNS?Yt.createElementNS((e||"http://www.w3.org/1999/xhtml").replace(/^https/,"http"),t):Yt.createElement(t);return i&&i.style?i:Yt.createElement(t)},xt=function n(t,e,i){var r=getComputedStyle(t);return r[e]||r.getPropertyValue(e.replace(_r,"-$1").toLowerCase())||r.getPropertyValue(e)||!i&&n(t,xe(e)||e,1)||""},Br="O,Moz,ms,Ms,Webkit".split(","),xe=function(t,e,i){var r=e||ae,a=r.style,s=5;if(t in a&&!i)return t;for(t=t.charAt(0).toUpperCase()+t.substr(1);s--&&!(Br[s]+t in a););return s<0?null:(s===3?"ms":s>=0?Br[s]:"")+t},Fi=function(){ws()&&window.document&&(zr=window,Yt=zr.document,me=Yt.documentElement,ae=ji("div")||{style:{}},ji("div"),H=xe(H),ht=H+"Origin",ae.style.cssText="border-width:0;line-height:0;position:absolute;padding:0",Ja=!!xe("perspective"),vr=gt.core.reverting,gr=1)},Vr=function(t){var e=t.ownerSVGElement,i=ji("svg",e&&e.getAttribute("xmlns")||"http://www.w3.org/2000/svg"),r=t.cloneNode(!0),a;r.style.display="block",i.appendChild(r),me.appendChild(i);try{a=r.getBBox()}catch{}return i.removeChild(r),me.removeChild(i),a},Ur=function(t,e){for(var i=e.length;i--;)if(t.hasAttribute(e[i]))return t.getAttribute(e[i])},Ka=function(t){var e,i;try{e=t.getBBox()}catch{e=Vr(t),i=1}return e&&(e.width||e.height)||i||(e=Vr(t)),e&&!e.width&&!e.x&&!e.y?{x:+Ur(t,["x","cx","x1"])||0,y:+Ur(t,["y","cy","y1"])||0,width:0,height:0}:e},Qa=function(t){return!!(t.getCTM&&(!t.parentNode||t.ownerSVGElement)&&Ka(t))},Jt=function(t,e){if(e){var i=t.style,r;e in Bt&&e!==ht&&(e=H),i.removeProperty?(r=e.substr(0,2),(r==="ms"||e.substr(0,6)==="webkit")&&(e="-"+e),i.removeProperty(r==="--"?e:e.replace(_r,"-$1").toLowerCase())):i.removeAttribute(e)}},Gt=function(t,e,i,r,a,s){var o=new mt(t._pt,e,i,0,1,s?Xa:Ga);return t._pt=o,o.b=r,o.e=a,t._props.push(i),o},Yr={deg:1,rad:1,turn:1},qs={grid:1,flex:1},Kt=function n(t,e,i,r){var a=parseFloat(i)||0,s=(i+"").trim().substr((a+"").length)||"px",o=ae.style,l=Ss.test(e),c=t.tagName.toLowerCase()==="svg",d=(c?"client":"offset")+(l?"Width":"Height"),f=100,p=r==="px",m=r==="%",h,u,v,y;if(r===s||!a||Yr[r]||Yr[s])return a;if(s!=="px"&&!p&&(a=n(t,e,i,"px")),y=t.getCTM&&Qa(t),(m||s==="%")&&(Bt[e]||~e.indexOf("adius")))return h=y?t.getBBox()[l?"width":"height"]:t[d],Q(m?a/h*f:a/100*h);if(o[l?"width":"height"]=f+(p?s:r),u=r!=="rem"&&~e.indexOf("adius")||r==="em"&&t.appendChild&&!c?t:t.parentNode,y&&(u=(t.ownerSVGElement||{}).parentNode),(!u||u===Yt||!u.appendChild)&&(u=Yt.body),v=u._gsap,v&&m&&v.width&&l&&v.time===bt.time&&!v.uncache)return Q(a/v.width*f);if(m&&(e==="height"||e==="width")){var b=t.style[e];t.style[e]=f+r,h=t[d],b?t.style[e]=b:Jt(t,e)}else(m||s==="%")&&!qs[xt(u,"display")]&&(o.position=xt(t,"position")),u===t&&(o.position="static"),u.appendChild(ae),h=ae[d],u.removeChild(ae),o.position="absolute";return l&&m&&(v=ne(u),v.time=bt.time,v.width=u[d]),Q(p?h*a/f:h&&a?f/h*a:0)},jt=function(t,e,i,r){var a;return gr||Fi(),e in Rt&&e!=="transform"&&(e=Rt[e],~e.indexOf(",")&&(e=e.split(",")[0])),Bt[e]&&e!=="transform"?(a=Ne(t,r),a=e!=="transformOrigin"?a[e]:a.svg?a.origin:ri(xt(t,ht))+" "+a.zOrigin+"px"):(a=t.style[e],(!a||a==="auto"||r||~(a+"").indexOf("calc("))&&(a=ii[e]&&ii[e](t,e,i)||xt(t,e)||ga(t,e)||(e==="opacity"?1:0))),i&&!~(a+"").trim().indexOf(" ")?Kt(t,e,a,i)+i:a},zs=function(t,e,i,r){if(!i||i==="none"){var a=xe(e,t,1),s=a&&xt(t,a,1);s&&s!==i?(e=a,i=s):e==="borderColor"&&(i=xt(t,"borderTopColor"))}var o=new mt(this._pt,t.style,e,0,1,Va),l=0,c=0,d,f,p,m,h,u,v,y,b,k,T,w;if(o.b=i,o.e=r,i+="",r+="",r.substring(0,6)==="var(--"&&(r=xt(t,r.substring(4,r.indexOf(")")))),r==="auto"&&(u=t.style[e],t.style[e]=r,r=xt(t,e)||r,u?t.style[e]=u:Jt(t,e)),d=[i,r],Ra(d),i=d[0],r=d[1],p=i.match(ue)||[],w=r.match(ue)||[],w.length){for(;f=ue.exec(r);)v=f[0],b=r.substring(l,f.index),h?h=(h+1)%5:(b.substr(-5)==="rgba("||b.substr(-5)==="hsla(")&&(h=1),v!==(u=p[c++]||"")&&(m=parseFloat(u)||0,T=u.substr((m+"").length),v.charAt(1)==="="&&(v=pe(m,v)+T),y=parseFloat(v),k=v.substr((y+"").length),l=ue.lastIndex-k.length,k||(k=k||$t.units[e]||T,l===r.length&&(r+=k,o.e+=k)),T!==k&&(m=Kt(t,e,u,k)||0),o._pt={_next:o._pt,p:b||c===1?b:",",s:m,c:y-m,m:h&&h<4||e==="zIndex"?Math.round:0});o.c=l<r.length?r.substring(l,r.length):""}else o.r=e==="display"&&r==="none"?Xa:Ga;return ua.test(r)&&(o.e=0),this._pt=o,o},Gr={top:"0%",bottom:"100%",left:"0%",right:"100%",center:"50%"},js=function(t){var e=t.split(" "),i=e[0],r=e[1]||"50%";return(i==="top"||i==="bottom"||r==="left"||r==="right")&&(t=i,i=r,r=t),e[0]=Gr[i]||i,e[1]=Gr[r]||r,e.join(" ")},Fs=function(t,e){if(e.tween&&e.tween._time===e.tween._dur){var i=e.t,r=i.style,a=e.u,s=i._gsap,o,l,c;if(a==="all"||a===!0)r.cssText="",l=1;else for(a=a.split(","),c=a.length;--c>-1;)o=a[c],Bt[o]&&(l=1,o=o==="transformOrigin"?ht:H),Jt(i,o);l&&(Jt(i,H),s&&(s.svg&&i.removeAttribute("transform"),r.scale=r.rotate=r.translate="none",Ne(i,1),s.uncache=1,Wa(r)))}},ii={clearProps:function(t,e,i,r,a){if(a.data!=="isFromStart"){var s=t._pt=new mt(t._pt,e,i,0,0,Fs);return s.u=r,s.pr=-10,s.tween=a,t._props.push(i),1}}},Me=[1,0,0,1,0,0],Za={},tn=function(t){return t==="matrix(1, 0, 0, 1, 0, 0)"||t==="none"||!t},Xr=function(t){var e=xt(t,H);return tn(e)?Me:e.substr(7).match(da).map(Q)},br=function(t,e){var i=t._gsap||ne(t),r=t.style,a=Xr(t),s,o,l,c;return i.svg&&t.getAttribute("transform")?(l=t.transform.baseVal.consolidate().matrix,a=[l.a,l.b,l.c,l.d,l.e,l.f],a.join(",")==="1,0,0,1,0,0"?Me:a):(a===Me&&!t.offsetParent&&t!==me&&!i.svg&&(l=r.display,r.display="block",s=t.parentNode,(!s||!t.offsetParent&&!t.getBoundingClientRect().width)&&(c=1,o=t.nextElementSibling,me.appendChild(t)),a=Xr(t),l?r.display=l:Jt(t,"display"),c&&(o?s.insertBefore(t,o):s?s.appendChild(t):me.removeChild(t))),e&&a.length>6?[a[0],a[1],a[4],a[5],a[12],a[13]]:a)},Bi=function(t,e,i,r,a,s){var o=t._gsap,l=a||br(t,!0),c=o.xOrigin||0,d=o.yOrigin||0,f=o.xOffset||0,p=o.yOffset||0,m=l[0],h=l[1],u=l[2],v=l[3],y=l[4],b=l[5],k=e.split(" "),T=parseFloat(k[0])||0,w=parseFloat(k[1])||0,D,A,O,_;i?l!==Me&&(A=m*v-h*u)&&(O=T*(v/A)+w*(-u/A)+(u*b-v*y)/A,_=T*(-h/A)+w*(m/A)-(m*b-h*y)/A,T=O,w=_):(D=Ka(t),T=D.x+(~k[0].indexOf("%")?T/100*D.width:T),w=D.y+(~(k[1]||k[0]).indexOf("%")?w/100*D.height:w)),r||r!==!1&&o.smooth?(y=T-c,b=w-d,o.xOffset=f+(y*m+b*u)-y,o.yOffset=p+(y*h+b*v)-b):o.xOffset=o.yOffset=0,o.xOrigin=T,o.yOrigin=w,o.smooth=!!r,o.origin=e,o.originIsAbsolute=!!i,t.style[ht]="0px 0px",s&&(Gt(s,o,"xOrigin",c,T),Gt(s,o,"yOrigin",d,w),Gt(s,o,"xOffset",f,o.xOffset),Gt(s,o,"yOffset",p,o.yOffset)),t.setAttribute("data-svg-origin",T+" "+w)},Ne=function(t,e){var i=t._gsap||new Na(t);if("x"in i&&!e&&!i.uncache)return i;var r=t.style,a=i.scaleX<0,s="px",o="deg",l=getComputedStyle(t),c=xt(t,ht)||"0",d,f,p,m,h,u,v,y,b,k,T,w,D,A,O,_,x,C,S,L,g,$,I,P,M,U,ct,dt,at,Ot,z,G;return d=f=p=u=v=y=b=k=T=0,m=h=1,i.svg=!!(t.getCTM&&Qa(t)),l.translate&&((l.translate!=="none"||l.scale!=="none"||l.rotate!=="none")&&(r[H]=(l.translate!=="none"?"translate3d("+(l.translate+" 0 0").split(" ").slice(0,3).join(", ")+") ":"")+(l.rotate!=="none"?"rotate("+l.rotate+") ":"")+(l.scale!=="none"?"scale("+l.scale.split(" ").join(",")+") ":"")+(l[H]!=="none"?l[H]:"")),r.scale=r.rotate=r.translate="none"),A=br(t,i.svg),i.svg&&(i.uncache?(M=t.getBBox(),c=i.xOrigin-M.x+"px "+(i.yOrigin-M.y)+"px",P=""):P=!e&&t.getAttribute("data-svg-origin"),Bi(t,P||c,!!P||i.originIsAbsolute,i.smooth!==!1,A)),w=i.xOrigin||0,D=i.yOrigin||0,A!==Me&&(C=A[0],S=A[1],L=A[2],g=A[3],d=$=A[4],f=I=A[5],A.length===6?(m=Math.sqrt(C*C+S*S),h=Math.sqrt(g*g+L*L),u=C||S?de(S,C)*re:0,b=L||g?de(L,g)*re+u:0,b&&(h*=Math.abs(Math.cos(b*he))),i.svg&&(d-=w-(w*C+D*L),f-=D-(w*S+D*g))):(G=A[6],Ot=A[7],ct=A[8],dt=A[9],at=A[10],z=A[11],d=A[12],f=A[13],p=A[14],O=de(G,at),v=O*re,O&&(_=Math.cos(-O),x=Math.sin(-O),P=$*_+ct*x,M=I*_+dt*x,U=G*_+at*x,ct=$*-x+ct*_,dt=I*-x+dt*_,at=G*-x+at*_,z=Ot*-x+z*_,$=P,I=M,G=U),O=de(-L,at),y=O*re,O&&(_=Math.cos(-O),x=Math.sin(-O),P=C*_-ct*x,M=S*_-dt*x,U=L*_-at*x,z=g*x+z*_,C=P,S=M,L=U),O=de(S,C),u=O*re,O&&(_=Math.cos(O),x=Math.sin(O),P=C*_+S*x,M=$*_+I*x,S=S*_-C*x,I=I*_-$*x,C=P,$=M),v&&Math.abs(v)+Math.abs(u)>359.9&&(v=u=0,y=180-y),m=Q(Math.sqrt(C*C+S*S+L*L)),h=Q(Math.sqrt(I*I+G*G)),O=de($,I),b=Math.abs(O)>2e-4?O*re:0,T=z?1/(z<0?-z:z):0),i.svg&&(P=t.getAttribute("transform"),i.forceCSS=t.setAttribute("transform","")||!tn(xt(t,H)),P&&t.setAttribute("transform",P))),Math.abs(b)>90&&Math.abs(b)<270&&(a?(m*=-1,b+=u<=0?180:-180,u+=u<=0?180:-180):(h*=-1,b+=b<=0?180:-180)),e=e||i.uncache,i.x=d-((i.xPercent=d&&(!e&&i.xPercent||(Math.round(t.offsetWidth/2)===Math.round(-d)?-50:0)))?t.offsetWidth*i.xPercent/100:0)+s,i.y=f-((i.yPercent=f&&(!e&&i.yPercent||(Math.round(t.offsetHeight/2)===Math.round(-f)?-50:0)))?t.offsetHeight*i.yPercent/100:0)+s,i.z=p+s,i.scaleX=Q(m),i.scaleY=Q(h),i.rotation=Q(u)+o,i.rotationX=Q(v)+o,i.rotationY=Q(y)+o,i.skewX=b+o,i.skewY=k+o,i.transformPerspective=T+s,(i.zOrigin=parseFloat(c.split(" ")[2])||!e&&i.zOrigin||0)&&(r[ht]=ri(c)),i.xOffset=i.yOffset=0,i.force3D=$t.force3D,i.renderTransform=i.svg?Vs:Ja?en:Bs,i.uncache=0,i},ri=function(t){return(t=t.split(" "))[0]+" "+t[1]},wi=function(t,e,i){var r=st(e);return Q(parseFloat(e)+parseFloat(Kt(t,"x",i+"px",r)))+r},Bs=function(t,e){e.z="0px",e.rotationY=e.rotationX="0deg",e.force3D=0,en(t,e)},ee="0deg",we="0px",ie=") ",en=function(t,e){var i=e||this,r=i.xPercent,a=i.yPercent,s=i.x,o=i.y,l=i.z,c=i.rotation,d=i.rotationY,f=i.rotationX,p=i.skewX,m=i.skewY,h=i.scaleX,u=i.scaleY,v=i.transformPerspective,y=i.force3D,b=i.target,k=i.zOrigin,T="",w=y==="auto"&&t&&t!==1||y===!0;if(k&&(f!==ee||d!==ee)){var D=parseFloat(d)*he,A=Math.sin(D),O=Math.cos(D),_;D=parseFloat(f)*he,_=Math.cos(D),s=wi(b,s,A*_*-k),o=wi(b,o,-Math.sin(D)*-k),l=wi(b,l,O*_*-k+k)}v!==we&&(T+="perspective("+v+ie),(r||a)&&(T+="translate("+r+"%, "+a+"%) "),(w||s!==we||o!==we||l!==we)&&(T+=l!==we||w?"translate3d("+s+", "+o+", "+l+") ":"translate("+s+", "+o+ie),c!==ee&&(T+="rotate("+c+ie),d!==ee&&(T+="rotateY("+d+ie),f!==ee&&(T+="rotateX("+f+ie),(p!==ee||m!==ee)&&(T+="skew("+p+", "+m+ie),(h!==1||u!==1)&&(T+="scale("+h+", "+u+ie),b.style[H]=T||"translate(0, 0)"},Vs=function(t,e){var i=e||this,r=i.xPercent,a=i.yPercent,s=i.x,o=i.y,l=i.rotation,c=i.skewX,d=i.skewY,f=i.scaleX,p=i.scaleY,m=i.target,h=i.xOrigin,u=i.yOrigin,v=i.xOffset,y=i.yOffset,b=i.forceCSS,k=parseFloat(s),T=parseFloat(o),w,D,A,O,_;l=parseFloat(l),c=parseFloat(c),d=parseFloat(d),d&&(d=parseFloat(d),c+=d,l+=d),l||c?(l*=he,c*=he,w=Math.cos(l)*f,D=Math.sin(l)*f,A=Math.sin(l-c)*-p,O=Math.cos(l-c)*p,c&&(d*=he,_=Math.tan(c-d),_=Math.sqrt(1+_*_),A*=_,O*=_,d&&(_=Math.tan(d),_=Math.sqrt(1+_*_),w*=_,D*=_)),w=Q(w),D=Q(D),A=Q(A),O=Q(O)):(w=f,O=p,D=A=0),(k&&!~(s+"").indexOf("px")||T&&!~(o+"").indexOf("px"))&&(k=Kt(m,"x",s,"px"),T=Kt(m,"y",o,"px")),(h||u||v||y)&&(k=Q(k+h-(h*w+u*A)+v),T=Q(T+u-(h*D+u*O)+y)),(r||a)&&(_=m.getBBox(),k=Q(k+r/100*_.width),T=Q(T+a/100*_.height)),_="matrix("+w+","+D+","+A+","+O+","+k+","+T+")",m.setAttribute("transform",_),b&&(m.style[H]=_)},Us=function(t,e,i,r,a){var s=360,o=et(a),l=parseFloat(a)*(o&&~a.indexOf("rad")?re:1),c=l-r,d=r+c+"deg",f,p;return o&&(f=a.split("_")[1],f==="short"&&(c%=s,c!==c%(s/2)&&(c+=c<0?s:-s)),f==="cw"&&c<0?c=(c+s*Fr)%s-~~(c/s)*s:f==="ccw"&&c>0&&(c=(c-s*Fr)%s-~~(c/s)*s)),t._pt=p=new mt(t._pt,e,i,r,c,Es),p.e=d,p.u="deg",t._props.push(i),p},Wr=function(t,e){for(var i in e)t[i]=e[i];return t},Ys=function(t,e,i){var r=Wr({},i._gsap),a="perspective,force3D,transformOrigin,svgOrigin",s=i.style,o,l,c,d,f,p,m,h;r.svg?(c=i.getAttribute("transform"),i.setAttribute("transform",""),s[H]=e,o=Ne(i,1),Jt(i,H),i.setAttribute("transform",c)):(c=getComputedStyle(i)[H],s[H]=e,o=Ne(i,1),s[H]=c);for(l in Bt)c=r[l],d=o[l],c!==d&&a.indexOf(l)<0&&(m=st(c),h=st(d),f=m!==h?Kt(i,l,c,h):parseFloat(c),p=parseFloat(d),t._pt=new mt(t._pt,o,l,f,p-f,zi),t._pt.u=h||0,t._props.push(l));Wr(o,r)};pt("padding,margin,Width,Radius",function(n,t){var e="Top",i="Right",r="Bottom",a="Left",s=(t<3?[e,i,r,a]:[e+a,e+i,r+i,r+a]).map(function(o){return t<2?n+o:"border"+o+n});ii[t>1?"border"+n:n]=function(o,l,c,d,f){var p,m;if(arguments.length<4)return p=s.map(function(h){return jt(o,h,c)}),m=p.join(" "),m.split(p[0]).length===5?p[0]:m;p=(d+"").split(" "),m={},s.forEach(function(h,u){return m[h]=p[u]=p[u]||p[(u-1)/2|0]}),o.init(l,m,f)}});var rn={name:"css",register:Fi,targetTest:function(t){return t.style&&t.nodeType},init:function(t,e,i,r,a){var s=this._props,o=t.style,l=i.vars.startAt,c,d,f,p,m,h,u,v,y,b,k,T,w,D,A,O,_;gr||Fi(),this.styles=this.styles||Ha(t),O=this.styles.props,this.tween=i;for(u in e)if(u!=="autoRound"&&(d=e[u],!(_t[u]&&qa(u,e,i,r,t,a)))){if(m=typeof d,h=ii[u],m==="function"&&(d=d.call(i,r,t,a),m=typeof d),m==="string"&&~d.indexOf("random(")&&(d=Le(d)),h)h(this,t,u,d,i)&&(A=1);else if(u.substr(0,2)==="--")c=(getComputedStyle(t).getPropertyValue(u)+"").trim(),d+="",Wt.lastIndex=0,Wt.test(c)||(v=st(c),y=st(d),y?v!==y&&(c=Kt(t,u,c,y)+y):v&&(d+=v)),this.add(o,"setProperty",c,d,r,a,0,0,u),s.push(u),O.push(u,0,o[u]);else if(m!=="undefined"){if(l&&u in l?(c=typeof l[u]=="function"?l[u].call(i,r,t,a):l[u],et(c)&&~c.indexOf("random(")&&(c=Le(c)),st(c+"")||c==="auto"||(c+=$t.units[u]||st(jt(t,u))||""),(c+"").charAt(1)==="="&&(c=jt(t,u))):c=jt(t,u),p=parseFloat(c),b=m==="string"&&d.charAt(1)==="="&&d.substr(0,2),b&&(d=d.substr(2)),f=parseFloat(d),u in Rt&&(u==="autoAlpha"&&(p===1&&jt(t,"visibility")==="hidden"&&f&&(p=0),O.push("visibility",0,o.visibility),Gt(this,o,"visibility",p?"inherit":"hidden",f?"inherit":"hidden",!f)),u!=="scale"&&u!=="transform"&&(u=Rt[u],~u.indexOf(",")&&(u=u.split(",")[0]))),k=u in Bt,k){if(this.styles.save(u),_=d,m==="string"&&d.substring(0,6)==="var(--"){if(d=xt(t,d.substring(4,d.indexOf(")"))),d.substring(0,5)==="calc("){var x=t.style.perspective;t.style.perspective=d,d=xt(t,"perspective"),x?t.style.perspective=x:Jt(t,"perspective")}f=parseFloat(d)}if(T||(w=t._gsap,w.renderTransform&&!e.parseTransform||Ne(t,e.parseTransform),D=e.smoothOrigin!==!1&&w.smooth,T=this._pt=new mt(this._pt,o,H,0,1,w.renderTransform,w,0,-1),T.dep=1),u==="scale")this._pt=new mt(this._pt,w,"scaleY",w.scaleY,(b?pe(w.scaleY,b+f):f)-w.scaleY||0,zi),this._pt.u=0,s.push("scaleY",u),u+="X";else if(u==="transformOrigin"){O.push(ht,0,o[ht]),d=js(d),w.svg?Bi(t,d,0,D,0,this):(y=parseFloat(d.split(" ")[2])||0,y!==w.zOrigin&&Gt(this,w,"zOrigin",w.zOrigin,y),Gt(this,o,u,ri(c),ri(d)));continue}else if(u==="svgOrigin"){Bi(t,d,1,D,0,this);continue}else if(u in Za){Us(this,w,u,p,b?pe(p,b+d):d);continue}else if(u==="smoothOrigin"){Gt(this,w,"smooth",w.smooth,d);continue}else if(u==="force3D"){w[u]=d;continue}else if(u==="transform"){Ys(this,d,t);continue}}else u in o||(u=xe(u)||u);if(k||(f||f===0)&&(p||p===0)&&!ks.test(d)&&u in o)v=(c+"").substr((p+"").length),f||(f=0),y=st(d)||(u in $t.units?$t.units[u]:v),v!==y&&(p=Kt(t,u,c,y)),this._pt=new mt(this._pt,k?w:o,u,p,(b?pe(p,b+f):f)-p,!k&&(y==="px"||u==="zIndex")&&e.autoRound!==!1?Cs:zi),this._pt.u=y||0,k&&_!==d?(this._pt.b=c,this._pt.e=_,this._pt.r=As):v!==y&&y!=="%"&&(this._pt.b=c,this._pt.r=Ts);else if(u in o)zs.call(this,t,u,c,b?b+d:d);else if(u in t)this.add(t,u,c||t[u],b?b+d:d,r,a);else if(u!=="parseTransform"){sr(u,d);continue}k||(u in o?O.push(u,0,o[u]):typeof t[u]=="function"?O.push(u,2,t[u]()):O.push(u,1,c||t[u])),s.push(u)}}A&&Ua(this)},render:function(t,e){if(e.tween._time||!vr())for(var i=e._pt;i;)i.r(t,i.d),i=i._next;else e.styles.revert()},get:jt,aliases:Rt,getSetter:function(t,e,i){var r=Rt[e];return r&&r.indexOf(",")<0&&(e=r),e in Bt&&e!==ht&&(t._gsap.x||jt(t,"x"))?i&&jr===i?e==="scale"?Ls:Ds:(jr=i||{})&&(e==="scale"?Is:Rs):t.style&&!rr(t.style[e])?Os:~e.indexOf("-")?Ps:mr(t,e)},core:{_removeProperty:Jt,_getMatrix:br}};gt.utils.checkPrefix=xe;gt.core.getStyleSaver=Ha;(function(n,t,e,i){var r=pt(n+","+t+","+e,function(a){Bt[a]=1});pt(t,function(a){$t.units[a]="deg",Za[a]=1}),Rt[r[13]]=n+","+t,pt(i,function(a){var s=a.split(":");Rt[s[1]]=r[s[0]]})})("x,y,z,scale,scaleX,scaleY,xPercent,yPercent","rotation,rotationX,rotationY,skewX,skewY","transform,transformOrigin,svgOrigin,force3D,smoothOrigin,transformPerspective","0:translateX,1:translateY,2:translateZ,8:rotate,8:rotationZ,8:rotateZ,9:rotateX,10:rotateY");pt("x,y,z,top,right,bottom,left,width,height,fontSize,padding,margin,perspective",function(n){$t.units[n]="px"});gt.registerPlugin(rn);var Dt=gt.registerPlugin(rn)||gt;Dt.core.Tween;const te=()=>{var n;return(n=window.matchMedia)==null?void 0:n.call(window,"(prefers-reduced-motion: reduce)").matches};function ci(n,t){var e;return Array.from(((e=n==null?void 0:n.querySelectorAll)==null?void 0:e.call(n,t))||[])}function Gs(n){!n||te()||Dt.fromTo(n,{autoAlpha:0,y:46,scale:.94,rotate:-.8},{autoAlpha:1,y:0,scale:1,rotate:0,duration:.72,ease:"back.out(1.55)"})}function Xs(n){if(!n||te())return;Dt.timeline({defaults:{ease:"back.out(1.7)"}}).fromTo(".login-header",{y:-44,autoAlpha:0,rotate:-1.5},{y:0,autoAlpha:1,rotate:0,duration:.72}).fromTo(".login-logo",{scale:.15,rotate:-32},{scale:1,rotate:0,duration:.72},"-=0.35").fromTo(".login-header h1, .login-header p",{y:24,autoAlpha:0,skewY:3},{y:0,autoAlpha:1,skewY:0,stagger:.08,duration:.5},"-=0.35").fromTo(".login-card",{y:70,autoAlpha:0,scale:.88,rotate:1.2},{y:0,autoAlpha:1,scale:1,rotate:0,duration:.82},"-=0.22").fromTo(".demo-chip, .field, .login-forgot, #login-btn, .login-footer-text, .login-link",{y:22,autoAlpha:0,scale:.92},{y:0,autoAlpha:1,scale:1,stagger:.055,duration:.38},"-=0.42")}function an(n){if(!n||te())return;const t=ci(n,".card, .quick-card, .idea-item, .project-card, .collab-card, .profile-header, .guideline-card, .empty-state-card");t.length&&Dt.fromTo(t.slice(0,12),{y:34,autoAlpha:0,scale:.88,rotate:-1.2},{y:0,autoAlpha:1,scale:1,rotate:0,stagger:.055,duration:.56,ease:"back.out(1.8)"})}function Ws(n){if(!n||te())return;const t=n.querySelector(".overlay-screen, .chat-screen");t&&(Dt.fromTo(t,{x:80,autoAlpha:0,scale:.9,rotate:1.5},{x:0,autoAlpha:1,scale:1,rotate:0,duration:.58,ease:"back.out(1.55)"}),an(t))}function Hs(n){if(!n||te())return;const t=n.querySelector(".drawer-panel.open"),e=n.querySelector(".drawer-backdrop.open");e&&Dt.fromTo(e,{autoAlpha:0},{autoAlpha:1,duration:.28}),t&&(Dt.fromTo(t,{x:80,autoAlpha:0,scale:.96},{x:0,autoAlpha:1,scale:1,duration:.48,ease:"back.out(1.6)"}),Dt.fromTo(ci(t,".notif-card"),{x:28,autoAlpha:0,scale:.92},{x:0,autoAlpha:1,scale:1,stagger:.055,duration:.36,delay:.08,ease:"back.out(1.8)"}))}function Js(n){!n||te()||ci(n,".bar-chart .bar").forEach((t,e)=>{const i=t.style.height;Dt.fromTo(t,{height:"4%",scaleY:.4},{height:i,scaleY:1,duration:.8,delay:e*.07,ease:"elastic.out(1, 0.55)"})})}function ai(n){!n||te()||Dt.fromTo(n,{scale:.88,rotate:-1.5},{scale:1,rotate:0,duration:.34,ease:"elastic.out(1, 0.45)"})}function Hr(n){if(!n||te())return;const t=ci(n,".chat-bubble"),e=t[t.length-1];e&&Dt.fromTo(e,{y:22,autoAlpha:0,scale:.82,rotate:1.5},{y:0,autoAlpha:1,scale:1,rotate:0,duration:.42,ease:"back.out(2)"})}function yr(n){Ws(n),Hs(n),an(n),Js(n)}function Ks(n){let t="login",e="",i="",r="";const a=[{emoji:"🛣️",title:"Nossa trajetória",image:"https://images.unsplash.com/photo-1758873269117-d5845126928a?auto=format&fit=crop&w=900&q=70",text:"A InnovateCorp nasceu para transformar ideias do dia a dia em melhorias reais. Cada sugestão vira parte de uma trilha: registro, curadoria, priorização, projeto e aprendizado para o time inteiro."},{emoji:"🧩",title:"O que fazemos",image:"https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=900&q=70",text:"Conectamos operadores, gestores e liderança em um workspace simples: ideias, orientações, projetos, ranking de participação, colaboradores e chat. Tudo pensado para reduzir ruído e acelerar decisões."},{emoji:"💬",title:"Feedbacks",image:"https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=900&q=70",text:"Feedback não fica perdido. O gestor consegue orientar, a liderança cria diretrizes e o operador acompanha o que aconteceu com cada ideia. O ciclo fica visível, humano e contínuo."}];function s(){return`
    <div class="login-screen">
      <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
      <div class="login-header">
        <div class="login-logo">⚡</div>
        <h1>InnovateCorp</h1>
        <p>Um workspace vivo para ideias, projetos e decisões.</p>
      </div>
      <form class="login-card" id="login-form">
        <h2>👋 Entrar na sua conta</h2>
        <p class="login-hint">
          Escolha um perfil demo abaixo. O que você editar fica salvo localmente no navegador.
        </p>
        <div class="demo-chips">
          ${Hi.map(f=>`
            <button type="button" class="demo-chip" data-email="${f.email}" data-pass="${f.password}">
              ${f.role==="leader"?"Líder":f.role==="manager"?"Gestor":"Operador"}
            </button>
          `).join("")}
        </div>
        <div class="field">
          <label for="email">E-mail</label>
          <input id="email" name="email" type="email" placeholder="seu@email.com" autocomplete="username" />
        </div>
        <div class="field">
          <label for="password">Senha</label>
          <input id="password" name="password" type="password" autocomplete="current-password" />
        </div>
        <button type="button" class="login-forgot">Esqueceu sua senha?</button>
        ${i?`<div class="login-success">${i}</div>`:""}
        <div id="login-error" class="login-error" ${r?"":"hidden"}>${r}</div>
        <button type="submit" class="btn-primary" id="login-btn">Entrar</button>
        <p class="login-footer-text">Primeira vez aqui?</p>
        <button type="button" class="login-link" data-action="about">Conheça o InnovateCorp</button>
      </form>
      <p class="login-copyright">© 2026 InnovateCorp. Todos os direitos reservados.</p>
    </div>
  `}function o(){return`
      <div class="login-screen about-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <div class="about-hero">
          <button type="button" class="about-back" data-action="back-login">← Voltar</button>
          <span class="about-kicker">⚡ InnovateCorp</span>
          <h1>Um lugar para ideias saírem do corredor e virarem projeto.</h1>
          <p>Conheça o fluxo por trás do app: pessoas, contexto e feedback no mesmo workspace.</p>
        </div>
        <div class="about-sections">
          ${a.map(f=>`
            <article class="about-card reveal-on-scroll">
              <img src="${f.image}" alt="${f.title}" loading="lazy" />
              <div>
                <span class="about-topic">${f.emoji} ${f.title}</span>
                <p>${f.text}</p>
              </div>
            </article>
          `).join("")}
        </div>
        <button type="button" class="btn-primary about-register" data-action="register">Criar minha conta</button>
      </div>
    `}function l(){return`
      <div class="login-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <form class="login-card auth-flow-card" id="register-form">
          <button type="button" class="about-back inline" data-action="about">← Voltar</button>
          <h2>🪪 Criar cadastro</h2>
          <p class="login-hint">Este cadastro é local, sem e-mail externo. Depois você entra normalmente usando o e-mail e senha criados aqui.</p>
          <div class="field"><label>Nome completo</label><input name="fullName" required placeholder="Seu nome" /></div>
          <div class="field"><label>E-mail</label><input name="email" type="email" required placeholder="voce@empresa.com" /></div>
          <div class="field"><label>Senha</label><input name="password" type="password" required minlength="4" /></div>
          <div class="field">
            <label>Perfil</label>
            <select name="role">
              <option value="operator">Operador</option>
              <option value="manager">Gestor</option>
              <option value="leader">Líder</option>
            </select>
          </div>
          ${r?`<div class="login-error">${r}</div>`:""}
          <button type="submit" class="btn-primary">Cadastrar e voltar ao login</button>
        </form>
      </div>
    `}function c(){return`
      <div class="login-screen">
        <button type="button" class="login-theme-toggle" data-action="theme-toggle" aria-label="Alternar tema">◐</button>
        <form class="login-card auth-flow-card" id="reset-form">
          <button type="button" class="about-back inline" data-action="back-login">← Voltar</button>
          <h2>🔐 Alterar senha</h2>
          <p class="login-hint">Sem SMTP e sem envio de e-mail: informe o e-mail cadastrado abaixo e escolha uma nova senha. Tudo fica salvo localmente neste navegador.</p>
          <div class="reset-email-banner">E-mail para recuperação: <strong>${e||"preencha abaixo"}</strong></div>
          <div class="field"><label>E-mail cadastrado</label><input name="email" type="email" required value="${e}" /></div>
          <div class="field"><label>Nova senha</label><input name="password" type="password" required minlength="4" /></div>
          ${r?`<div class="login-error">${r}</div>`:""}
          <button type="submit" class="btn-primary">Salvar nova senha</button>
        </form>
      </div>
    `}function d(){return t==="about"?o():t==="register"?l():t==="reset"?c():s()}return{get html(){return d()},mount(f){const p=()=>{f.innerHTML=d(),h(f)},m=()=>{const u=f.querySelectorAll(".reveal-on-scroll");if(!u.length)return;const v=new IntersectionObserver(y=>{y.forEach(b=>{b.isIntersecting&&b.target.classList.add("visible")})},{threshold:.25});u.forEach(y=>v.observe(y))},h=u=>{var w,D,A,O;Xs(f),m();const v=u.querySelector("#login-form"),y=u.querySelector("#login-error"),b=u.querySelector("#login-btn"),k=u.querySelector("#email"),T=u.querySelector("#password");u.querySelectorAll('[data-action="about"]').forEach(_=>_.addEventListener("click",()=>{t="about",r="",p()})),(w=u.querySelector('[data-action="register"]'))==null||w.addEventListener("click",()=>{t="register",r="",p()}),u.querySelectorAll('[data-action="back-login"]').forEach(_=>_.addEventListener("click",()=>{t="login",r="",p()})),(D=u.querySelector(".login-forgot"))==null||D.addEventListener("click",()=>{var _;e=((_=k==null?void 0:k.value)==null?void 0:_.trim())||"",t="reset",r="",p()}),(A=u.querySelector("#register-form"))==null||A.addEventListener("submit",_=>{_.preventDefault();const x=new FormData(_.target),C=bn({fullName:x.get("fullName"),email:x.get("email"),password:x.get("password"),role:x.get("role")});if(!C.ok){r=C.error,p();return}t="login",i="Cadastro criado. Agora entre usando seu e-mail e senha.",r="",p()}),(O=u.querySelector("#reset-form"))==null||O.addEventListener("submit",_=>{_.preventDefault();const x=new FormData(_.target),C=yn(x.get("email"),x.get("password"));if(!C.ok){r=C.error,p();return}e=x.get("email"),t="login",i=`Senha alterada para ${e}. Faça login com a nova senha.`,r="",p()}),u.querySelectorAll(".demo-chip").forEach(_=>_.addEventListener("click",()=>{ai(_),k.value=_.dataset.email,T.value=_.dataset.pass,y.hidden=!0})),v==null||v.addEventListener("submit",_=>{_.preventDefault();const x=k.value,C=T.value;if(!x.includes("@")){y.textContent="Informe um e-mail válido.",y.hidden=!1;return}if(!C){y.textContent="Informe a senha.",y.hidden=!1;return}b.disabled=!0,b.textContent="Entrando…",ai(b),setTimeout(()=>{const S=xn(x,C);if(b.disabled=!1,b.textContent="Entrar",!S.ok){y.textContent=S.error,y.hidden=!1;return}y.hidden=!0,n(S.account)},320)})};h(f)}}}const Vi="innovatecorp_ideas",nn="innovatecorp_profiles",sn="innovatecorp_suggestions",Ui="innovatecorp_guidelines",Yi="innovatecorp_projects",on="innovatecorp_messages",xr="innovatecorp_notif_read",$r="innovatecorp_manager_notif_read",wr="innovatecorp_leader_notif_read",ln=[{id:"demo-operador",email:"operador@innovatecorp.com",name:"Ana Silva",role:"operator"},{id:"demo-gestor",email:"gestor@innovatecorp.com",name:"Carlos Mendes",role:"manager"},{id:"demo-lider",email:"lideranca@innovatecorp.com",name:"Mariana Costa",role:"leader"}],Qt=[{id:"mock-op-joao",name:"João Pereira",email:"joao.pereira@innovatecorp.com"},{id:"mock-op-maria",name:"Maria Santos",email:"maria.santos@innovatecorp.com"},{id:"mock-op-pedro",name:"Pedro Oliveira",email:"pedro.oliveira@innovatecorp.com"},{id:"mock-op-lucia",name:"Lúcia Ferreira",email:"lucia.ferreira@innovatecorp.com"},{id:"mock-op-rafael",name:"Rafael Costa",email:"rafael.costa@innovatecorp.com"},{id:"demo-operador",name:"Ana Silva",email:"operador@innovatecorp.com"}],Jr=[{id:"g1",title:"Transformação Digital",content:"Priorizar iniciativas que digitalizem processos operacionais e reduzam retrabalho.",authorName:"Liderança InnovateCorp",createdAt:Date.now()-864e5*14},{id:"g2",title:"Sustentabilidade Corporativa",content:"Incentivar ideias com impacto ambiental mensurável e metas ESG claras.",authorName:"Liderança InnovateCorp",createdAt:Date.now()-864e5*7}],Si=[{id:"seed-1",title:"App mobile para fila express",description:"Atender clientes em fila com app dedicado, reduzindo tempo médio de espera.",category:"Tecnologia",authorEmail:"joao.pereira@innovatecorp.com",authorName:"João Pereira",authorId:"mock-op-joao",status:"pending",createdAt:Date.now()-864e5*2},{id:"seed-2",title:"Treinamento gamificado de vendas",description:"Gamificar o onboarding e treinamento contínuo das equipes de vendas.",category:"Processo",authorEmail:"maria.santos@innovatecorp.com",authorName:"Maria Santos",authorId:"mock-op-maria",status:"prioritized",createdAt:Date.now()-864e5*4},{id:"seed-3",title:"Etiquetas digitais no estoque",description:"Substituir etiquetas físicas por etiquetas digitais para controle em tempo real.",category:"Tecnologia",authorEmail:"pedro.oliveira@innovatecorp.com",authorName:"Pedro Oliveira",authorId:"mock-op-pedro",status:"approved",createdAt:Date.now()-864e5*6},{id:"seed-4",title:"Coleta seletiva nas lojas",description:"Programa de coleta seletiva em todas as filiais, integrando consumidores.",category:"Sustentabilidade",authorEmail:"lucia.ferreira@innovatecorp.com",authorName:"Lúcia Ferreira",authorId:"mock-op-lucia",status:"pending",createdAt:Date.now()-864e5*1},{id:"seed-5",title:"Painel de metas por equipe",description:"Dashboard com metas e progresso visível para todas as equipes.",category:"Processo",authorEmail:"rafael.costa@innovatecorp.com",authorName:"Rafael Costa",authorId:"mock-op-rafael",status:"approved",createdAt:Date.now()-864e5*8},{id:"seed-6",title:"Reduzir fila no caixa",description:"Pré-atendimento e sinalização nas filas longas do caixa.",category:"Outro",authorEmail:"operador@innovatecorp.com",authorName:"Ana Silva",authorId:"demo-operador",status:"pending",createdAt:Date.now()-864e5*3},{id:"seed-7",title:"Programa de mentoria interna",description:"Mentoria entre operadores experientes e novos colaboradores.",category:"Processo",authorEmail:"maria.santos@innovatecorp.com",authorName:"Maria Santos",authorId:"mock-op-maria",status:"pending",createdAt:Date.now()-864e5*.5},{id:"seed-8",title:"Wi-Fi exclusivo para clientes",description:"Rede separada de alta qualidade, com landing page de fidelidade.",category:"Tecnologia",authorEmail:"joao.pereira@innovatecorp.com",authorName:"João Pereira",authorId:"mock-op-joao",status:"prioritized",createdAt:Date.now()-864e5*10},{id:"seed-9",title:"Mural digital de reconhecimento",description:"Tela na loja exibindo destaques do time semanalmente.",category:"Processo",authorEmail:"lucia.ferreira@innovatecorp.com",authorName:"Lúcia Ferreira",authorId:"mock-op-lucia",status:"approved",createdAt:Date.now()-864e5*12},{id:"seed-10",title:"Embalagens 100% recicláveis",description:"Substituir todas as embalagens internas por material reciclável.",category:"Sustentabilidade",authorEmail:"pedro.oliveira@innovatecorp.com",authorName:"Pedro Oliveira",authorId:"mock-op-pedro",status:"pending",createdAt:Date.now()-864e5*.2},{id:"seed-11",title:"Caixa rápido com tablet",description:"Atendentes itinerantes com tablet para reduzir fila no horário de pico.",category:"Tecnologia",authorEmail:"rafael.costa@innovatecorp.com",authorName:"Rafael Costa",authorId:"mock-op-rafael",status:"pending",createdAt:Date.now()-864e5*.8},{id:"seed-12",title:"Treinamento de atendimento empático",description:"Workshop trimestral com foco em escuta ativa e empatia.",category:"Processo",authorEmail:"joao.pereira@innovatecorp.com",authorName:"João Pereira",authorId:"mock-op-joao",status:"approved",createdAt:Date.now()-864e5*14},{id:"seed-13",title:"Reciclagem de uniformes antigos",description:"Programa de troca de uniformes por descontos em produtos parceiros.",category:"Sustentabilidade",authorEmail:"maria.santos@innovatecorp.com",authorName:"Maria Santos",authorId:"mock-op-maria",status:"pending",createdAt:Date.now()-864e5*1.2}],Kr=[{id:"proj-1",title:"Checklist digital",description:"App de checklist diário com fotos e validação em tempo real do gestor.",category:"Tecnologia",owner:"João Pereira",status:"execution",investment:45e3,profit:12e3,deadlineEpoch:Date.now()+864e5*12,createdAt:Date.now()-864e5*30},{id:"proj-2",title:"Treinamento gamificado",description:"Plataforma com missões e ranking para acelerar onboarding das equipes.",category:"Processo",owner:"Maria Santos",status:"planning",investment:75e3,profit:0,deadlineEpoch:Date.now()+864e5*60,createdAt:Date.now()-864e5*15},{id:"proj-3",title:"Etiquetas digitais",description:"Substituição completa de etiquetas físicas por e-ink com integração ERP.",category:"Tecnologia",owner:"Pedro Oliveira",status:"urgent",investment:95e3,profit:18500,deadlineEpoch:Date.now()+864e5*3,createdAt:Date.now()-864e5*45},{id:"proj-4",title:"Coleta seletiva nas lojas",description:"Programa de coleta seletiva consolidado em 6 filiais piloto.",category:"Sustentabilidade",owner:"Lúcia Ferreira",status:"completed",investment:22e3,profit:6e3,deadlineEpoch:Date.now()-864e5*7,createdAt:Date.now()-864e5*90}],Gi=[{id:"planning",label:"Planejamento",color:"#6b7280"},{id:"execution",label:"Em execução",color:"#3b82f6"},{id:"urgent",label:"Prazo urgente",color:"#ef4444"},{id:"risk",label:"Ticket médio",color:"#f59e0b"},{id:"completed",label:"Concluído",color:"#10b981"}];function Qr(n){return Gi.find(t=>t.id===n)||Gi[0]}function cn(){try{return JSON.parse(localStorage.getItem(nn)||"{}")}catch{return{}}}function Qs(n){localStorage.setItem(nn,JSON.stringify(n))}function di(n){const t=n.email.toLowerCase(),e=cn()[t]||{},i=e.fullName||n.fullName,r=e.email||n.email,a=e.name||i.split(" ")[0]||n.name;return{...n,accountKey:t,name:a,fullName:i,email:r,avatar:e.avatar||null,avatarColor:e.avatarColor||Zs(n.role)}}function Zs(n){switch(n){case"manager":return"#EC4899";case"leader":return"#7C3AED";default:return"#6C5CE7"}}function Sr(n,{fullName:t,email:e,avatar:i,avatarColor:r}){const a=cn(),s=a[n]||{},o=t.split(" ")[0]||t;a[n]={fullName:t.trim(),email:e.trim().toLowerCase(),name:o,avatar:i??s.avatar??null,avatarColor:r??s.avatarColor??"#6C5CE7"},Qs(a);const l=s.email||n,c=a[n].email;if(l!==c){const d=qe().map(f=>f.authorEmail===l?{...f,authorEmail:c,authorName:t}:f);ze(d)}else{const d=qe().map(f=>f.authorEmail===c?{...f,authorName:t}:f);ze(d)}return a[n]}function qe(){try{const n=localStorage.getItem(Vi);if(n){const t=JSON.parse(n),e=new Set(t.map(r=>r.id)),i=Si.filter(r=>!e.has(r.id));if(i.length){const r=[...i,...t];return ze(r),r}return t}}catch{}return localStorage.setItem(Vi,JSON.stringify(Si)),[...Si]}function ze(n){localStorage.setItem(Vi,JSON.stringify(n))}function Ct(){return qe().sort((n,t)=>t.createdAt-n.createdAt)}function dn(n){return Ct().filter(t=>t.authorEmail===n)}function Fe(n){const t=Qt.find(e=>e.email===n.authorEmail||e.id===n.authorId);return(t==null?void 0:t.name)||n.authorName||"Colaborador"}function ni(){return un(Ct())}function Je(){const n=new Date,t=new Date(n.getFullYear(),n.getMonth(),1).getTime();return un(Ct().filter(e=>e.createdAt>=t))}function un(n){const t=new Map;return Qt.forEach(e=>{t.set(e.email,{key:e.email,name:e.name,email:e.email,submitted:0,approved:0})}),n.forEach(e=>{const i=e.authorEmail||e.authorId;t.has(i)||t.set(i,{key:i,name:Fe(e),email:e.authorEmail,submitted:0,approved:0});const r=t.get(i);r.submitted++,(e.status==="approved"||e.status==="prioritized")&&r.approved++}),[...t.values()].sort((e,i)=>i.submitted-e.submitted||i.approved-e.approved||e.name.localeCompare(i.name))}function fn({title:n,description:t,category:e,authorEmail:i,authorName:r,authorId:a}){const s=qe(),o={id:`idea-${Date.now()}`,title:n.trim(),description:t.trim(),category:e,authorEmail:i,authorName:r,authorId:a||i,status:"pending",createdAt:Date.now()};return s.unshift(o),ze(s),Be(),hn(),Er(),o}function Zr(n,t){const e=qe(),i=e.findIndex(r=>r.id===n);i>=0&&(e[i]={...e[i],status:t},ze(e),Be(),Er())}function ui(n){return{pending:"Pendente",approved:"Aprovada",rejected:"Reprovada",prioritized:"Priorizada"}[n]||n}function fi(n){return`status-${n}`}function pi(){try{const n=localStorage.getItem(Ui);if(n)return JSON.parse(n)}catch{}return localStorage.setItem(Ui,JSON.stringify(Jr)),[...Jr]}function kr(n){localStorage.setItem(Ui,JSON.stringify(n))}function Nt(){return pi().sort((n,t)=>t.createdAt-n.createdAt)}function to({title:n,content:t,authorName:e}){const i=pi(),r={id:`gl-${Date.now()}`,title:n.trim(),content:t.trim(),authorName:e,createdAt:Date.now()};return i.unshift(r),kr(i),Be(),r}function eo(n,{title:t,content:e}){const i=pi(),r=i.findIndex(a=>a.id===n);r>=0&&(i[r]={...i[r],title:t.trim(),content:e.trim()},kr(i),Be())}function io(n){const t=pi().filter(e=>e.id!==n);kr(t)}new Proxy([],{get(n,t){return Nt()[t]},has(n,t){return t in Nt()},ownKeys(){return Object.keys(Nt())},getOwnPropertyDescriptor(){return{enumerable:!0,configurable:!0}}});function pn(){try{return JSON.parse(localStorage.getItem(sn)||"[]")}catch{return[]}}function ro(n){localStorage.setItem(sn,JSON.stringify(n))}function ao(n){return pn().filter(t=>t.targetEmail===n).sort((t,e)=>e.createdAt-t.createdAt)}function mn({senderName:n,senderRole:t,targetId:e,targetEmail:i,targetName:r,message:a}){const s=pn(),o={id:`sug-${Date.now()}`,senderName:n,senderRole:t||"manager",managerName:n,targetId:e,targetEmail:i,targetName:r,message:a.trim(),createdAt:Date.now()};return s.unshift(o),ro(s),Be(),hn(),Er(),o}function no(n){const t=dn(n),e=Nt(),i=[];return e.forEach(r=>{i.push({id:`guideline-${r.id}`,title:"Orientação da liderança",body:`${r.title}: ${r.content}`,icon:"campaign",createdAt:r.createdAt})}),ao(n).forEach(r=>{const a=r.senderRole==="leader"?"Liderança":"Gestor";i.push({id:`sug-${r.id}`,title:`Sugestão do ${a.toLowerCase()}`,body:`${r.senderName||r.managerName}: ${r.message}`,icon:r.senderRole==="leader"?"workspace_premium":"campaign",createdAt:r.createdAt})}),t.forEach(r=>{r.status==="pending"?i.push({id:`pending-${r.id}`,title:"Ideia enviada",body:`Sua ideia "${r.title}" está aguardando curadoria do gestor.`,icon:"schedule",createdAt:r.createdAt}):r.status==="approved"?i.push({id:`approved-${r.id}`,title:"Ideia aprovada",body:`Parabéns! "${r.title}" foi aprovada. Confira na aba Ideias.`,icon:"check_circle",createdAt:r.createdAt}):r.status==="rejected"?i.push({id:`rejected-${r.id}`,title:"Ideia reprovada",body:`A ideia "${r.title}" foi reprovada.`,icon:"priority_high",createdAt:r.createdAt}):r.status==="prioritized"&&i.push({id:`prio-${r.id}`,title:"Ideia priorizada",body:`Sua ideia "${r.title}" foi priorizada pela gestão.`,icon:"lightbulb",createdAt:r.createdAt})}),i.sort((r,a)=>a.createdAt-r.createdAt)}function Be(){localStorage.removeItem(xr)}function so(){return localStorage.getItem(xr)!=="1"}function oo(){localStorage.setItem(xr,"1")}function lo(){const n=Ct(),t=[],e=n.filter(r=>r.status==="pending").length;e>0&&t.push({id:"pending",title:"Ideias pendentes",body:`${e} ideia(s) aguardam curadoria.`,icon:"warning",createdAt:Date.now()}),n.filter(r=>r.status==="pending").slice(0,5).forEach(r=>{t.push({id:`idea-${r.id}`,title:`Nova ideia — ${Fe(r)}`,body:`"${r.title}" aguarda avaliação.`,icon:"lightbulb",createdAt:r.createdAt})});const i=ni()[0];return i&&t.push({id:"top",title:"Operador mais ativo",body:`${i.name} lidera com ${i.submitted} ideia(s).`,icon:"groups",createdAt:Date.now()-36e5}),t.sort((r,a)=>a.createdAt-r.createdAt)}function co(){return localStorage.getItem($r)!=="1"}function uo(){localStorage.setItem($r,"1")}function hn(){localStorage.removeItem($r)}function fo(){const n=[],t=Ct(),e=t.filter(a=>a.status==="pending").length,i=t.filter(a=>a.status==="approved"||a.status==="prioritized").length;n.push({id:"overview",title:"Visão estratégica",body:`${t.length} ideias registradas · ${e} pendentes · ${i} aprovadas.`,icon:"insights",createdAt:Date.now()});const r=Je()[0];return r&&n.push({id:"monthly-top",title:"Destaque do mês",body:`${r.name} enviou ${r.submitted} ideia(s) neste mês.`,icon:"emoji_events",createdAt:Date.now()-6e4}),t.filter(a=>a.status==="pending").slice(0,3).forEach(a=>{n.push({id:`idea-${a.id}`,title:"Ideia pendente de curadoria",body:`"${a.title}" — ${Fe(a)}`,icon:"lightbulb",createdAt:a.createdAt})}),n.sort((a,s)=>s.createdAt-a.createdAt)}function po(){return localStorage.getItem(wr)!=="1"}function mo(){localStorage.setItem(wr,"1")}function Er(){localStorage.removeItem(wr)}function gn(){try{const n=localStorage.getItem(Yi);if(n)return JSON.parse(n)}catch{}return localStorage.setItem(Yi,JSON.stringify(Kr)),[...Kr]}function ho(n){localStorage.setItem(Yi,JSON.stringify(n))}function go(){return gn().sort((n,t)=>(n.status==="completed"?1:-1)-(t.status==="completed"?1:-1)||t.createdAt-n.createdAt)}function vo(n,t){const e=gn(),i=e.findIndex(r=>r.id===n);return i>=0?(e[i]={...e[i],...t},ho(e),e[i]):null}const ta=["Combinado! Vou começar agora.","Obrigado pela mensagem! Vou avaliar.","Anotado, qualquer dúvida te chamo.","Excelente ideia, vou aplicar.","Pode contar comigo!","Vou alinhar com o time e retorno."];function Xi(){try{return JSON.parse(localStorage.getItem(on)||"[]")}catch{return[]}}function ea(n){localStorage.setItem(on,JSON.stringify(n))}function vn(n,t){return[n,t].sort().join("|")}function _o(n,t){const e=vn(n,t);return Xi().filter(i=>i.threadId===e).sort((i,r)=>i.createdAt-r.createdAt)}function bo({fromId:n,fromName:t,toId:e,toName:i,text:r}){const a=r.trim();if(!a)return null;const s=Xi(),o={id:`msg-${Date.now()}-${Math.random().toString(36).slice(2,6)}`,threadId:vn(n,e),fromId:n,fromName:t,toId:e,toName:i,text:a,createdAt:Date.now()};return s.push(o),ea(s),Qt.some(c=>c.id===e)&&!ln.some(c=>c.id===e)&&setTimeout(()=>{const c=ta[Math.floor(Math.random()*ta.length)],d=Xi();d.push({id:`msg-${Date.now()}-${Math.random().toString(36).slice(2,6)}`,threadId:o.threadId,fromId:e,fromName:i,toId:n,toName:t,text:c,createdAt:Date.now()}),ea(d),window.dispatchEvent(new CustomEvent("innovate:new-message",{detail:{threadId:o.threadId}}))},1500+Math.random()*1500),window.dispatchEvent(new CustomEvent("innovate:new-message",{detail:{threadId:o.threadId}})),o}function ia(n){const t=[];return ln.forEach(e=>{e.email.toLowerCase()!==(n==null?void 0:n.toLowerCase())&&t.push({id:e.id,name:e.name,email:e.email,role:e.role,isMock:!1})}),Qt.forEach(e=>{e.id!=="demo-operador"&&e.email.toLowerCase()!==(n==null?void 0:n.toLowerCase())&&t.push({id:e.id,name:e.name,email:e.email,role:"operator",isMock:!0})}),t}function ra(n){return Ct().filter(t=>t.authorId===n.id||t.authorEmail===n.email)}function Tr({currentUser:n,onClose:t}){let e="list",i=null,r=null;function a(){const h=ia(n.accountKey);return`
      <div class="overlay-screen">
        ${Mt("👥 Colaboradores")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">
            Um painel de pessoas como no Notion: veja ideias, contexto e converse sem sair do fluxo.
          </p>
          ${h.length===0?it("Nenhum colaborador disponível.","groups"):h.map(s).join("")}
        </div>
      </div>
    `}function s(h){const u=ra(h);return`
      <div class="collab-card" data-collab-id="${h.id}">
        ${mi(h.name,hi(h.role))}
        <div class="collab-info">
          <strong>${E(h.name)}</strong>
          <div class="meta">${E(h.email)}</div>
          ${wn(h.role)}
          <div class="meta" style="margin-top:6px">
            💡 ${u.length} ideia(s)
          </div>
        </div>
        <div class="actions">
          <button type="button" data-action="open-chat" data-collab-id="${h.id}" aria-label="Conversar">
            ${R("chat")}
          </button>
        </div>
      </div>
    `}function o(){if(!i)return a();const h=ra(i);return`
      <div class="overlay-screen">
        ${Mt(i.name)}
        <div class="screen-scroll">
          <div class="collab-detail-header">
            ${mi(i.name,hi(i.role),84)}
            <strong>${E(i.name)}</strong>
            <p>${E(i.email)}</p>
            <span class="role-pill">${Ce(i.role)}</span>
            <button type="button" class="chat-btn" data-action="open-chat" data-collab-id="${i.id}">
              ${R("chat")} Conversar
            </button>
          </div>
          <p class="section-title">💡 Ideias enviadas</p>
          ${h.length===0?it("Este colaborador ainda não enviou ideias.","lightbulb"):h.map(u=>`
                <div class="idea-item">
                  <strong>${E(u.title)}</strong>
                  <div class="meta">${E(u.category)} · ${Pt(u.createdAt)}</div>
                  <p>${E(u.description||"")}</p>
                  <span class="status ${fi(u.status)}">${ui(u.status)}</span>
                </div>
              `).join("")}
        </div>
      </div>
    `}function l(){if(!i)return a();const h=_o(n.id,i.id);return`
      <div class="chat-screen">
        <header class="chat-header">
          <button type="button" class="icon-btn" data-action="back-from-chat" aria-label="Voltar">${R("arrow_back")}</button>
          <div class="chat-header-info">
            ${mi(i.name,hi(i.role),38)}
            <div>
              <strong>${E(i.name)}</strong>
              <div class="meta">${E(Ce(i.role)||"")}${i.isMock?" · responde automaticamente":""}</div>
            </div>
          </div>
        </header>
        <div class="chat-body" id="chat-body">
          ${h.length===0?`<p class="chat-empty">${R("forum")}<br>Diga olá para começar a conversa.</p>`:h.map(u=>`
                <div class="chat-bubble ${u.fromId===n.id?"me":"them"}">
                  ${E(u.text)}
                  <time>${Pt(u.createdAt)}</time>
                </div>
              `).join("")}
        </div>
        <form class="chat-composer" id="chat-form">
          <input type="text" id="chat-input" placeholder="Escreva uma mensagem..." autocomplete="off" />
          <button type="submit" class="send-btn" aria-label="Enviar">${R("send")}</button>
        </form>
      </div>
    `}function c(){return e==="chat"?l():e==="detail"?o():a()}function d(h){return ia(n.accountKey).find(u=>u.id===h)||null}function f(h,u){var y;h.querySelectorAll(".collab-card").forEach(b=>{b.addEventListener("click",k=>{k.target.closest("[data-action]")||(ai(b),i=d(b.dataset.collabId),e="detail",u())})}),h.querySelectorAll('[data-action="open-chat"]').forEach(b=>{b.addEventListener("click",k=>{k.stopPropagation(),ai(b),i=d(b.dataset.collabId),e="chat",u(),setTimeout(p,30)})}),(y=h.querySelector('[data-action="back-from-chat"]'))==null||y.addEventListener("click",()=>{e=i?"detail":"list",u()}),h.querySelectorAll('[data-action="back"]').forEach(b=>b.addEventListener("click",()=>{e==="detail"?(e="list",u()):e==="list"&&(m(),t==null||t())}));const v=h.querySelector("#chat-form");v&&i&&v.addEventListener("submit",b=>{b.preventDefault();const k=v.querySelector("#chat-input"),T=k.value.trim();T&&(bo({fromId:n.id,fromName:n.fullName,toId:i.id,toName:i.name,text:T}),k.value="",u(),setTimeout(()=>{p(),Hr(document)},30))}),m(),e==="chat"&&(r=window.setInterval(()=>{u(),setTimeout(()=>{p(),Hr(document)},30)},1200))}function p(){const h=document.getElementById("chat-body");h&&(h.scrollTop=h.scrollHeight)}function m(){r&&(window.clearInterval(r),r=null)}return{html:c,bind:f,stop:m,isActive:()=>e!=="list"||i!=null,reset(){e="list",i=null,m()}}}const yo=[{id:"home",label:"Início",icon:"home"},{id:"strategies",label:"Estratégias",icon:"track_changes"},{id:"ideas",label:"Ideias",icon:"lightbulb"},{id:"profile",label:"Perfil",icon:"person"}],xo=["Processo","Produto","Tecnologia","Sustentabilidade","Outro"];function ki(n){return`
    <div class="idea-item">
      <strong>${E(n.title)}</strong>
      <div class="meta">${E(n.category)} · ${Pt(n.createdAt)}</div>
      <span class="status ${fi(n.status)}">${ui(n.status)}</span>
    </div>
  `}function $o(n,t){let e={...n},i="home",r=null,a=!1,s="Processo",o="",l=!1,c="",d=null;function f(){}function p(){return d||(d=Tr({currentUser:{id:"demo-operador",accountKey:e.accountKey,fullName:e.fullName,name:e.name},onClose:()=>{r=null,d==null||d.reset(),f()}}),d)}function m(){return dn(e.email)}function h(){return m().slice(0,3)}function u(){const _={approved:3,prioritized:3,pending:2,rejected:1};return[...m()].sort((x,C)=>(_[C.status]||0)-(_[x.status]||0)||C.createdAt-x.createdAt)}function v(){const _=no(e.email);return`
      <div class="drawer-backdrop ${a?"open":""}" data-action="close-drawer"></div>
      <aside class="drawer-panel ${a?"open":""}">
        <div class="drawer-header">
          <h2>${R("notifications")} Notificações</h2>
          <button type="button" class="icon-btn" data-action="close-drawer">${R("close")}</button>
        </div>
        <div class="drawer-body">
          ${_.length===0?`<div class="empty-state">${R("notifications_off")}<p style="margin:8px 0 0">Nenhuma notificação no momento.</p></div>`:_.map(x=>`
            <div class="notif-card">
              <strong>${R(x.icon)} ${E(x.title)}</strong>
              <p>${E(x.body)}</p>
              <time>${Pt(x.createdAt)}</time>
            </div>
          `).join("")}
        </div>
      </aside>
    `}function y(){const _=er("operator");return l?`
        <div class="card-gradient" style="margin-top:8px;text-align:center">
          <div class="profile-avatar-wrap">
            <label class="profile-avatar-wrap" style="cursor:pointer">
              ${ge(e)}
              <input type="file" accept="image/*" data-action="avatar-file" hidden />
            </label>
            <button type="button" class="profile-avatar-camera" data-action="avatar-file-btn">${R("photo_camera")}</button>
          </div>
          <div class="avatar-colors">
            ${_.map(x=>`
              <button type="button" class="avatar-color-dot ${e.avatarColor===x?"selected":""}" data-color="${x}" style="background:${x}" aria-label="Cor"></button>
            `).join("")}
          </div>
          <div class="profile-edit-fields" style="text-align:left;margin-top:12px">
            <label>Nome completo</label>
            <input id="profile-name" value="${E(e.fullName)}" />
            <label>E-mail</label>
            <input id="profile-email" type="email" value="${E(e.email)}" />
          </div>
          ${c?`<p class="form-error">${E(c)}</p>`:""}
          <div class="form-actions" style="margin-top:14px;justify-content:center">
            <button type="button" class="btn-secondary" data-action="profile-cancel">Cancelar</button>
            <button type="button" class="btn-primary" data-action="profile-save" style="width:auto;background:rgba(255,255,255,0.92);color:var(--accent-dark);box-shadow:none">Salvar</button>
          </div>
        </div>
      `:`
      <div class="card-gradient profile-header" style="margin-top:8px">
        <div class="profile-avatar-wrap">${ge(e)}</div>
        <strong>${E(e.fullName)}</strong>
        <p style="margin:4px 0 0;opacity:0.95;font-size:0.88rem">${E(e.email)}</p>
        <span class="role-pill">${Ce("operator")}</span>
        <div style="margin-top:14px">
          <button type="button" class="btn-secondary" data-action="profile-edit" style="background:rgba(255,255,255,0.95);color:var(--accent-dark);border:none">
            ${R("edit")} Editar perfil
          </button>
        </div>
      </div>
    `}function b(){const _=m();return`
      <div class="overlay-screen">
        ${Mt("Todas as suas ideias")}
        <div class="screen-scroll">
          ${_.length===0?it("Você ainda não enviou nenhuma ideia.","lightbulb"):_.map(ki).join("")}
        </div>
      </div>
    `}function k(){return`
      <div class="overlay-screen">
        ${Mt(s==="Outro"?"Reportar problema":"Nova ideia")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Descreva sua ideia ou problema. Após o envio, o gestor avaliará na curadoria.</p>
          <form class="form-card" id="submit-idea-form">
            <label for="idea-title">Título</label>
            <input id="idea-title" name="title" required minlength="3" placeholder="Resumo da ideia" />
            <label for="idea-desc">Descrição</label>
            <textarea id="idea-desc" name="description" required minlength="10" placeholder="Descreva com detalhes"></textarea>
            <label for="idea-cat">Categoria</label>
            <select id="idea-cat" name="category">
              ${xo.map(x=>`<option value="${x}" ${x===s?"selected":""}>${x}</option>`).join("")}
            </select>
            ${o?`<p class="form-error">${E(o)}</p>`:""}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `}function T(){return r==="allIdeas"?b():r==="submit"?k():r==="collaborators"?p().html():""}function w(_){const x=m(),C=x.filter(g=>g.status==="approved"||g.status==="prioritized").length,L=Nt()[0];return`
      <div class="card-gradient" style="margin-top:8px">
        <p class="workspace-title" style="margin:0;font-size:1.15rem"><span>✨</span> Olá, ${E(_.name)}!</p>
        <p style="margin:6px 0 0;opacity:0.92;font-size:0.88rem">Seu espaço para registrar ideias que viram movimento.</p>
        <div style="margin-top:16px;background:rgba(255,255,255,0.22);border-radius:12px;height:10px;overflow:hidden">
          <div style="width:${Math.min(x.length*12+5,100)}%;height:100%;background:#fff;border-radius:12px;transition:width 0.4s ease"></div>
        </div>
        <p style="margin:10px 0 0;font-size:0.78rem;opacity:0.88">Nível ${Math.max(1,Math.floor(x.length/3)+1)} · ${x.length*25} pontos</p>
      </div>
      <p class="section-title">⚡ Ações rápidas</p>
      <div class="row-2">
        <button type="button" class="quick-card" data-action="new-idea"><span class="emoji-badge">💡</span><span>Nova ideia</span></button>
        <button type="button" class="quick-card" data-action="report"><span class="emoji-badge">🚩</span><span>Reportar problema</span></button>
      </div>
      <div class="row-2" style="margin-top:12px">
        <button type="button" class="quick-card" data-action="collaborators"><span class="emoji-badge">👥</span><span>Colaboradores</span></button>
        <button type="button" class="quick-card" data-action="all-ideas"><span class="emoji-badge">🗂️</span><span>Minhas ideias</span></button>
      </div>
      <p class="section-title">📌 Indicadores</p>
      <div class="row-2">
        ${Et("Ideias enviadas",String(x.length),x.length?"+"+x.length+" total":"")}
        ${Et("Aprovadas",String(C),C?"+"+C:"")}
      </div>
      <div class="card card-hoverable" style="margin-top:16px">
        <strong>🌤️ Insight do dia</strong>
        <p class="muted-text" style="margin:10px 0 0">
          Equipes que registram ideias semanalmente têm 40% mais chance de aprovação.
        </p>
      </div>
      ${L?`
        <div class="card card-hoverable" style="margin-top:12px">
          <strong>📣 Orientação estratégica</strong>
          <p style="margin:8px 0 4px;font-weight:600">${E(L.title)}</p>
          <p class="muted-text" style="margin:0">${E(L.content)}</p>
        </div>
      `:""}
      ${Zi("📝 Suas ideias recentes",m().length>3?"Ver todas":"","all-ideas")}
      ${h().length===0?it("Comece enviando sua primeira ideia.","lightbulb"):h().map(ki).join("")}
    `}function D(){switch(i){case"home":return w(e);case"strategies":{const _=Nt();return`
          <p class="section-title">📚 Estratégias corporativas</p>
          ${_.length===0?it("Aguardando diretrizes da liderança.","menu_book"):_.map(x=>`
              <div class="card card-hoverable guideline-card" style="margin-bottom:12px">
                <span class="guideline-label">✅ Diretriz ativa</span>
                <strong>${E(x.title)}</strong>
                <p class="muted-text" style="margin:8px 0 0">${E(x.content)}</p>
                <div class="guideline-meta">${R("workspace_premium")} ${E(x.authorName||"Liderança")} · ${Pt(x.createdAt)}</div>
              </div>
            `).join("")}
        `}case"ideas":return`
          <p class="section-title">🗂️ Minhas ideias</p>
          ${u().length===0?it("Você ainda não enviou ideias. Toque em + Nova ideia.","lightbulb"):u().map(ki).join("")}
          <button type="button" class="btn-primary" data-action="new-idea" style="margin-top:18px">+ Nova ideia</button>
        `;case"profile":{const _=m(),x=_.filter(C=>C.status==="approved"||C.status==="prioritized").length;return`
          ${y()}
          <div class="profile-stats">
            ${Et("Ideias enviadas",String(_.length))}
            ${Et("Aprovadas",String(x))}
          </div>
          <div class="profile-insight-card">
            <strong>${R("auto_awesome")} Próximo passo</strong>
            <p>Envie uma ideia com impacto claro no atendimento ou operação para aumentar suas chances na curadoria.</p>
          </div>
          <p class="muted-text" style="margin-top:8px;text-align:center">
            ${R("lock")} Seus dados ficam salvos no navegador (mesmo em outra guia).
          </p>
          <button type="button" class="btn-primary btn-danger" data-action="logout" style="margin-top:16px">Sair da conta</button>
        `}default:return""}}const A={home:"Início",strategies:"Estratégias",ideas:"Ideias",profile:"Perfil"};function O(){return r?`<div class="phone-screen-inner" style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">${T()}</div>`:`
      <div style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">
        ${v()}
        ${Ki(A[i],{showBadge:so()})}
        <div class="screen-scroll">${D()}</div>
        ${Qi(yo,i)}
      </div>
    `}return{html:O(),mount(_){const x=()=>{_.innerHTML=O(),C(_),yr(_)};f=x;const C=S=>{var $,I,P,M,U,ct,dt,at,Ot;if(r==="collaborators"){p().bind(S,x);return}($=d==null?void 0:d.stop)==null||$.call(d),tr(S,z=>{i=z,r=null,x()}),(I=S.querySelector('[data-action="collaborators"]'))==null||I.addEventListener("click",()=>{p().reset(),r="collaborators",x()}),(P=S.querySelector('[data-action="notifications"]'))==null||P.addEventListener("click",()=>{oo(),a=!0,x()}),S.querySelectorAll('[data-action="close-drawer"]').forEach(z=>{z.addEventListener("click",()=>{a=!1,x()})}),(M=S.querySelector('[data-action="profile-edit"]'))==null||M.addEventListener("click",()=>{l=!0,c="",x()}),(U=S.querySelector('[data-action="profile-cancel"]'))==null||U.addEventListener("click",()=>{l=!1,e=di({...e,email:e.accountKey}),x()}),(ct=S.querySelector('[data-action="profile-save"]'))==null||ct.addEventListener("click",()=>{var V,K,lt,Vt;const z=((K=(V=S.querySelector("#profile-name"))==null?void 0:V.value)==null?void 0:K.trim())||"",G=((Vt=(lt=S.querySelector("#profile-email"))==null?void 0:lt.value)==null?void 0:Vt.trim())||"";if(z.length<2){c="Nome deve ter pelo menos 2 caracteres.",x();return}if(!G.includes("@")){c="Informe um e-mail válido.",x();return}const N=Sr(e.accountKey,{fullName:z,email:G,avatar:e.avatar,avatarColor:e.avatarColor});e={...e,...N},l=!1,c="",x()}),S.querySelectorAll("[data-color]").forEach(z=>{z.addEventListener("click",()=>{e.avatarColor=z.dataset.color,e.avatar=null,x()})});const L=S.querySelector('[data-action="avatar-file"]');(dt=S.querySelector('[data-action="avatar-file-btn"]'))==null||dt.addEventListener("click",()=>L==null?void 0:L.click()),L==null||L.addEventListener("change",()=>{var N;const z=(N=L.files)==null?void 0:N[0];if(!z)return;const G=new FileReader;G.onload=()=>{e.avatar=G.result,x()},G.readAsDataURL(z)}),(at=S.querySelector('[data-action="all-ideas"]'))==null||at.addEventListener("click",()=>{r="allIdeas",x()}),S.querySelectorAll('[data-action="new-idea"]').forEach(z=>{z.addEventListener("click",()=>{s="Processo",o="",r="submit",x()})}),(Ot=S.querySelector('[data-action="report"]'))==null||Ot.addEventListener("click",()=>{s="Outro",o="",r="submit",x()}),S.querySelectorAll('[data-action="back"]').forEach(z=>{z.addEventListener("click",()=>{r=null,o="",x()})}),S.querySelectorAll('[data-action="logout"]').forEach(z=>z.addEventListener("click",t));const g=S.querySelector("#submit-idea-form");g&&g.addEventListener("submit",z=>{z.preventDefault();const G=g.title.value.trim(),N=g.description.value.trim(),V=g.category.value;if(G.length<3){o="Título deve ter pelo menos 3 caracteres.",x();return}if(N.length<10){o="Descrição deve ter pelo menos 10 caracteres.",x();return}fn({title:G,description:N,category:V,authorEmail:e.email,authorName:e.fullName,authorId:e.accountKey}),r=null,o="",i="home",x()})};x()}}}const wo=[{id:"home",label:"Início",icon:"home"},{id:"curation",label:"Curadoria",icon:"lightbulb"},{id:"projects",label:"Projetos",icon:"work"},{id:"guidelines",label:"Orientações",icon:"menu_book"},{id:"profile",label:"Perfil",icon:"person"}],So=["Processo","Produto","Tecnologia","Sustentabilidade","Outro"];function ko(n,t){var L;let e={...n},i="home",r=null,a=!1,s="",o=!1,l="",c=((L=Qt[0])==null?void 0:L.id)||"",d=null,f=null,p=()=>{};function m(){return f||(f=Tr({currentUser:{id:"demo-gestor",accountKey:e.accountKey,fullName:e.fullName,name:e.name},onClose:()=>{r=null,f==null||f.reset(),p()}}),f)}function h(){const g=lo();return`
      <div class="drawer-backdrop ${a?"open":""}" data-action="close-drawer"></div>
      <aside class="drawer-panel ${a?"open":""}">
        <div class="drawer-header">
          <h2>${R("notifications")} Notificações</h2>
          <button type="button" class="icon-btn" data-action="close-drawer">${R("close")}</button>
        </div>
        <div class="drawer-body">
          ${g.length===0?`<div class="empty-state">${R("notifications_off")}<p>Sem notificações.</p></div>`:g.map($=>`
              <div class="notif-card">
                <strong>${R($.icon)} ${E($.title)}</strong>
                <p>${E($.body)}</p>
                ${$.createdAt?`<time>${Pt($.createdAt)}</time>`:""}
              </div>
            `).join("")}
        </div>
      </aside>
    `}function u(){const g=Ct(),$=g.filter(M=>M.status==="pending").length,I=g.filter(M=>M.status==="approved"||M.status==="prioritized").length,P=ni();return`
      <div class="card-gradient" style="margin-top:8px">
        <p class="workspace-title" style="margin:0;font-size:1.15rem"><span>🚗</span> Olá, ${E(e.name)}!</p>
        <p style="margin:6px 0 0;opacity:0.92;font-size:0.88rem">Seu quadro tático de ideias, pessoas e projetos.</p>
        <div class="row-2" style="margin-top:16px;gap:10px">
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">${$}</div>
            <div style="font-size:0.74rem;opacity:0.92">aguardando curadoria</div>
          </div>
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">${g.length}</div>
            <div style="font-size:0.74rem;opacity:0.92">ideias totais</div>
          </div>
        </div>
      </div>
      <p class="section-title">⚡ Ações rápidas</p>
      <div class="row-2">
        <button type="button" class="quick-card" data-action="create-idea"><span class="emoji-badge">💡</span><span>Criar ideia</span></button>
        <button type="button" class="quick-card" data-action="suggestion"><span class="emoji-badge">📨</span><span>Enviar sugestão</span></button>
      </div>
      <div class="row-2" style="margin-top:12px">
        <button type="button" class="quick-card" data-action="collaborators"><span class="emoji-badge">👥</span><span>Colaboradores</span></button>
        <button type="button" class="quick-card" data-action="team"><span class="emoji-badge">🏆</span><span>Ranking</span></button>
      </div>
      ${Zi("🏆 Operadores mais ativos",P.length>3?"Ver todos":"","team")}
      ${P.length===0?it("Nenhuma atividade registrada ainda.","groups"):P.slice(0,3).map((M,U)=>`
            <div class="idea-item rank-${U+1}">
              <strong>#${U+1} ${E(M.name)}</strong>
              <div class="meta">${M.submitted} enviadas · ${M.approved} aprovadas</div>
            </div>
          `).join("")}
      <div class="row-2" style="margin-top:14px">
        ${Et("Aprovadas",String(I),"no portfólio")}
        ${Et("Pendentes",String($),"requerem ação")}
      </div>
      <div class="card card-hoverable" style="margin-top:16px">
        <strong>📊 Volume de ideias por operador</strong>
        ${P.length===0?'<p class="muted-text" style="margin-top:8px">Sem dados para o gráfico.</p>':sa(P.slice(0,6).map(M=>M.submitted),P.slice(0,6).map(M=>v(M.name)))}
      </div>
    `}function v(g){return g?g.split(" ")[0]:""}function y(){const g=go();if(d){const $=g.find(I=>I.id===d);if($)return k($)}return g.length?`
      <p class="section-title">🚧 Projetos em execução</p>
      <p class="muted-text" style="margin:-6px 0 12px">Toque em um projeto para ver os detalhes e alterar o status.</p>
      ${g.map(b).join("")}
    `:it("Nenhum projeto cadastrado.","work")}function b(g){const $=Qr(g.status);return`
      <div class="project-card" data-project-id="${g.id}" style="--proj-color:${$.color}">
        <div class="head">
          <strong>${E(g.title)}</strong>
          <span class="proj-status" style="--proj-color:${$.color};background:${$.color}">
            ${R(T(g.status))} ${E($.label)}
          </span>
        </div>
        <p class="desc">${E(g.description)}</p>
        <div class="meta-row">
          <span>${R("person")} <strong>${E(g.owner)}</strong></span>
          <span>${R("event")} ${D(g.deadlineEpoch)}</span>
        </div>
      </div>
    `}function k(g){const $=Qr(g.status),I=w(g.investment),P=w(g.profit),M=g.investment>0?Math.round(g.profit/g.investment*100):0;return`
      <header class="overlay-top">
        <button type="button" class="icon-btn" data-action="back-to-projects" aria-label="Voltar">${R("arrow_back")}</button>
        <h2>${E(g.title)}</h2>
      </header>
      <div class="project-detail-card" style="margin-bottom:14px">
        <span class="proj-status" style="background:${$.color}">
          ${R(T(g.status))} ${E($.label)}
        </span>
        <p class="muted-text" style="margin:10px 0 0">${E(g.description)}</p>
        <div class="meta-row" style="margin-top:14px;display:grid;grid-template-columns:1fr 1fr;gap:10px">
          <div><strong>Responsável</strong><div class="muted-text">${E(g.owner)}</div></div>
          <div><strong>Categoria</strong><div class="muted-text">${E(g.category)}</div></div>
          <div><strong>Investimento</strong><div class="muted-text">${I}</div></div>
          <div><strong>Lucro obtido</strong><div class="muted-text">${P}</div></div>
          <div><strong>ROI</strong><div class="muted-text">${M}%</div></div>
          <div><strong>Prazo</strong><div class="muted-text">${D(g.deadlineEpoch)}</div></div>
        </div>
      </div>
      <p class="section-title">🎛️ Alterar status</p>
      <div class="status-pills">
        ${Gi.map(U=>`
            <button type="button" class="status-pill ${U.id===g.status?"active":""}" data-status="${U.id}" data-project-id="${g.id}" style="--swatch-color:${U.color}">
              <span class="swatch" style="background:${U.color}"></span>
              ${E(U.label)}
            </button>
          `).join("")}
      </div>
    `}function T(g){return{planning:"schedule",execution:"rocket_launch",urgent:"priority_high",risk:"warning",completed:"check_circle"}[g]||"flag"}function w(g){return(g||0).toLocaleString("pt-BR",{style:"currency",currency:"BRL",maximumFractionDigits:0})}function D(g){const $=Math.round((g-Date.now())/864e5),P=new Date(g).toLocaleDateString("pt-BR");return $<0?`${P} (atrasado)`:$===0?`${P} (hoje)`:`${P} (em ${$} dia${$>1?"s":""})`}function A(){const g=ni();return`
      <div class="overlay-screen">
        ${Mt("Operadores da equipe")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Ranking acumulado por ideias enviadas.</p>
          ${g.length===0?it("Sem operadores ativos.","groups"):g.map(($,I)=>`
                <div class="idea-item rank-${I+1}">
                  <strong>#${I+1} ${E($.name)}</strong>
                  <div class="meta">${E($.email||"")}</div>
                  <div class="meta">${$.submitted} enviadas · ${$.approved} aprovadas</div>
                </div>
              `).join("")}
        </div>
      </div>
    `}function O(){return`
      <div class="overlay-screen">
        ${Mt("Sugestão ao operador")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Envie uma orientação direta ao operador. Ele a verá em Notificações.</p>
          <form class="form-card" id="suggestion-form">
            <label>Operador</label>
            <select name="target">
              ${Qt.map(g=>`
                <option value="${g.id}" data-email="${g.email}" data-name="${g.name}" ${g.id===c?"selected":""}>
                  ${E(g.name)} (${E(g.email)})
                </option>
              `).join("")}
            </select>
            <label>Mensagem</label>
            <textarea name="message" required minlength="10" placeholder="Compartilhe um direcionamento..."></textarea>
            ${s?`<p class="form-error">${E(s)}</p>`:""}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `}function _(){return`
      <div class="overlay-screen">
        ${Mt("Registrar nova ideia")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Você também pode propor ideias que serão visíveis na curadoria.</p>
          <form class="form-card" id="create-idea-form">
            <label>Título</label>
            <input name="title" required minlength="3" placeholder="Resumo da ideia" />
            <label>Descrição</label>
            <textarea name="description" required minlength="10" placeholder="Descreva com detalhes"></textarea>
            <label>Categoria</label>
            <select name="category">${So.map(g=>`<option>${g}</option>`).join("")}</select>
            ${s?`<p class="form-error">${E(s)}</p>`:""}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `}function x(){const g=Ct(),$=g.filter(M=>M.status==="pending").length,I=g.filter(M=>M.status==="approved"||M.status==="prioritized").length,P=er("manager");return o?`
        <div class="card-gradient" style="margin-top:8px;text-align:center">
          <div class="profile-avatar-wrap">
            <label class="profile-avatar-wrap" style="cursor:pointer">
              ${ge(e)}
              <input type="file" accept="image/*" data-action="avatar-file" hidden />
            </label>
            <button type="button" class="profile-avatar-camera" data-action="avatar-file-btn">${R("photo_camera")}</button>
          </div>
          <div class="avatar-colors">
            ${P.map(M=>`
              <button type="button" class="avatar-color-dot ${e.avatarColor===M?"selected":""}" data-color="${M}" style="background:${M}"></button>
            `).join("")}
          </div>
          <div class="profile-edit-fields" style="text-align:left;margin-top:12px">
            <label>Nome completo</label>
            <input id="profile-name" value="${E(e.fullName)}" />
            <label>E-mail</label>
            <input id="profile-email" type="email" value="${E(e.email)}" />
          </div>
          ${l?`<p class="form-error">${E(l)}</p>`:""}
          <div class="form-actions" style="margin-top:14px;justify-content:center">
            <button type="button" class="btn-secondary" data-action="profile-cancel">Cancelar</button>
            <button type="button" class="btn-primary" data-action="profile-save" style="width:auto;background:rgba(255,255,255,0.92);color:var(--accent-dark);box-shadow:none">Salvar</button>
          </div>
        </div>
      `:`
      <div class="card-gradient profile-header" style="margin-top:8px">
        <div class="profile-avatar-wrap">${ge(e)}</div>
        <strong>${E(e.fullName)}</strong>
        <p style="margin:4px 0 0;opacity:0.95;font-size:0.88rem">${E(e.email)}</p>
        <span class="role-pill">${Ce("manager")}</span>
        <div style="margin-top:14px">
          <button type="button" class="btn-secondary" data-action="profile-edit" style="background:rgba(255,255,255,0.95);color:var(--accent-dark);border:none">
            ${R("edit")} Editar perfil
          </button>
        </div>
      </div>
      <div class="profile-stats">
        ${Et("Pendentes",String($))}
        ${Et("Aprovadas",String(I))}
      </div>
      <div class="profile-insight-card">
        <strong>${R("task_alt")} Ritmo de curadoria</strong>
        <p>Priorize ideias pendentes diariamente para manter o time engajado e reduzir gargalos.</p>
      </div>
      <p class="muted-text" style="margin-top:8px;text-align:center">
        ${R("lock")} Seus dados ficam salvos no navegador.
      </p>
      <button type="button" class="btn-primary btn-danger" data-action="logout" style="margin-top:16px">Sair</button>
    `}function C(){switch(i){case"home":return u();case"curation":{const g=Ct();return g.length?`
          <p class="section-title">🧭 Curadoria</p>
          <p class="muted-text" style="margin-top:-4px;margin-bottom:8px">Avalie e direcione as ideias enviadas pelos operadores.</p>
          ${g.map($=>`
            <div class="idea-item" data-idea-row="${$.id}">
              <strong>${E($.title)}</strong>
              <div class="meta">${R("person")} ${E(Fe($))} · ${E($.category)} · ${Pt($.createdAt)}</div>
              <p style="font-size:0.85rem;color:var(--innovate-text-secondary);margin-top:8px;line-height:1.45">${E($.description)}</p>
              <span class="status ${fi($.status)}">${ui($.status)}</span>
              <div style="margin-top:12px;display:flex;gap:6px;flex-wrap:wrap">
                <button type="button" class="chip-btn approve" data-status="approved" data-idea-id="${$.id}">${R("check")} Aprovar</button>
                <button type="button" class="chip-btn reject" data-status="rejected" data-idea-id="${$.id}">${R("close")} Reprovar</button>
                <button type="button" class="chip-btn prioritize" data-status="prioritized" data-idea-id="${$.id}">${R("star")} Priorizar</button>
              </div>
            </div>
          `).join("")}
        `:it("Nenhuma ideia registrada.","lightbulb")}case"projects":return y();case"guidelines":{const g=Nt();return`
          <p class="section-title">📚 Orientações da liderança</p>
          ${g.length===0?it("Aguardando diretrizes da liderança.","menu_book"):g.map($=>`
                <div class="card card-hoverable guideline-card" style="margin-bottom:10px">
                  <span class="guideline-label">📌 Orientação</span>
                  <strong>${E($.title)}</strong>
                  <p class="muted-text" style="margin:8px 0 0">${E($.content)}</p>
                  <div class="guideline-meta">${R("workspace_premium")} ${E($.authorName||"Liderança")} · ${Pt($.createdAt)}</div>
                </div>
              `).join("")}
        `}case"profile":return x();default:return""}}function S(){if(r==="team")return`<div class="phone-screen-inner">${A()}</div>`;if(r==="suggestion")return`<div class="phone-screen-inner">${O()}</div>`;if(r==="create")return`<div class="phone-screen-inner">${_()}</div>`;if(r==="collaborators")return`<div class="phone-screen-inner">${m().html()}</div>`;const g={home:"Início",curation:"Curadoria",projects:"Projetos",guidelines:"Orientações",profile:"Perfil"};return`
      <div style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">
        ${h()}
        ${Ki(g[i],{showBadge:co()})}
        <div class="screen-scroll">${C()}</div>
        ${Qi(wo,i)}
      </div>
    `}return{html:S(),mount(g){const $=()=>{g.innerHTML=S(),I(g),yr(g)};p=$;const I=P=>{var U,ct,dt,at,Ot,z,G,N,V,K,lt,Vt,$e;if(r==="collaborators"){m().bind(P,$);return}(U=f==null?void 0:f.stop)==null||U.call(f),tr(P,q=>{i=q,r=null,d=null,$()}),(ct=P.querySelector('[data-action="collaborators"]'))==null||ct.addEventListener("click",()=>{m().reset(),r="collaborators",$()}),P.querySelectorAll("[data-project-id]").forEach(q=>{q.classList.contains("project-card")&&q.addEventListener("click",()=>{d=q.dataset.projectId,$()})}),(dt=P.querySelector('[data-action="back-to-projects"]'))==null||dt.addEventListener("click",()=>{d=null,$()}),P.querySelectorAll(".status-pill[data-status][data-project-id]").forEach(q=>{q.addEventListener("click",Z=>{Z.stopPropagation(),vo(q.dataset.projectId,{status:q.dataset.status}),$()})}),(at=P.querySelector('[data-action="notifications"]'))==null||at.addEventListener("click",()=>{uo(),a=!0,$()}),P.querySelectorAll('[data-action="close-drawer"]').forEach(q=>q.addEventListener("click",()=>{a=!1,$()})),(Ot=P.querySelector('[data-action="team"]'))==null||Ot.addEventListener("click",()=>{r="team",$()}),(z=P.querySelector('[data-action="suggestion"]'))==null||z.addEventListener("click",()=>{r="suggestion",s="",$()}),(G=P.querySelector('[data-action="create-idea"]'))==null||G.addEventListener("click",()=>{r="create",s="",$()}),P.querySelectorAll('[data-action="back"]').forEach(q=>q.addEventListener("click",()=>{r=null,s="",$()})),P.querySelectorAll(".chip-btn[data-status][data-idea-id]").forEach(q=>{q.addEventListener("click",()=>{const Z=q.dataset.status,nt=q.dataset.ideaId,vt=P.querySelector(`.idea-item[data-idea-row="${nt}"]`);if(vt){vt.classList.remove("idea-fly-approve","idea-fly-reject","idea-fly-prioritize");const Ve=Z==="approved"?"idea-fly-approve":Z==="rejected"?"idea-fly-reject":"idea-fly-prioritize";vt.classList.add(Ve),vt.querySelectorAll("button").forEach(Ue=>Ue.disabled=!0),window.setTimeout(()=>{Zr(nt,Z),$()},580)}else Zr(nt,Z),$()})}),(N=P.querySelector('[data-action="profile-edit"]'))==null||N.addEventListener("click",()=>{o=!0,$()}),(V=P.querySelector('[data-action="profile-cancel"]'))==null||V.addEventListener("click",()=>{o=!1,e=di({...e,email:e.accountKey}),$()}),(K=P.querySelector('[data-action="profile-save"]'))==null||K.addEventListener("click",()=>{var vt,Ve,Ue,Ar;const q=((Ve=(vt=P.querySelector("#profile-name"))==null?void 0:vt.value)==null?void 0:Ve.trim())||"",Z=((Ar=(Ue=P.querySelector("#profile-email"))==null?void 0:Ue.value)==null?void 0:Ar.trim())||"";if(q.length<2||!Z.includes("@")){l="Dados inválidos.",$();return}const nt=Sr(e.accountKey,{fullName:q,email:Z,avatar:e.avatar,avatarColor:e.avatarColor});e={...e,...nt},o=!1,l="",$()}),P.querySelectorAll("[data-color]").forEach(q=>{q.addEventListener("click",()=>{e.avatarColor=q.dataset.color,e.avatar=null,$()})});const M=P.querySelector('[data-action="avatar-file"]');(lt=P.querySelector('[data-action="avatar-file-btn"]'))==null||lt.addEventListener("click",()=>M==null?void 0:M.click()),M==null||M.addEventListener("change",()=>{var nt;const q=(nt=M.files)==null?void 0:nt[0];if(!q)return;const Z=new FileReader;Z.onload=()=>{e.avatar=Z.result,$()},Z.readAsDataURL(q)}),P.querySelectorAll('[data-action="logout"]').forEach(q=>q.addEventListener("click",t)),(Vt=P.querySelector("#suggestion-form"))==null||Vt.addEventListener("submit",q=>{q.preventDefault();const Z=q.target,nt=Z.target.selectedOptions[0],vt=Z.message.value.trim();if(vt.length<10){s="Mensagem deve ter no mínimo 10 caracteres.",$();return}mn({senderName:e.fullName,senderRole:"manager",targetId:nt.value,targetEmail:nt.dataset.email,targetName:nt.dataset.name,message:vt}),c=nt.value,r=null,i="home",$()}),($e=P.querySelector("#create-idea-form"))==null||$e.addEventListener("submit",q=>{q.preventDefault();const Z=q.target,nt=Z.title.value.trim(),vt=Z.description.value.trim();if(nt.length<3||vt.length<10){s="Preencha título (3+) e descrição (10+).",$();return}fn({title:nt,description:vt,category:Z.category.value,authorEmail:e.email,authorName:e.fullName,authorId:"manager-"+e.accountKey}),r=null,i="home",$()})};$()}}}const Eo=[{id:"home",label:"Executivo",icon:"dashboard"},{id:"guidelines",label:"Diretrizes",icon:"menu_book"},{id:"team",label:"Equipe",icon:"groups"},{id:"tracking",label:"Acompanhar",icon:"track_changes"},{id:"profile",label:"Perfil",icon:"person"}],Ei=new Intl.DateTimeFormat("pt-BR",{month:"long"});function To(n,t){var x;let e={...n},i="home",r=null,a=!1,s=null,o="",l=((x=Qt[0])==null?void 0:x.id)||"",c="",d=!1,f="",p=null,m=()=>{};function h(){return p||(p=Tr({currentUser:{id:"demo-lider",accountKey:e.accountKey,fullName:e.fullName,name:e.name},onClose:()=>{r=null,p==null||p.reset(),m()}}),p)}function u(){const C=fo();return`
      <div class="drawer-backdrop ${a?"open":""}" data-action="close-drawer"></div>
      <aside class="drawer-panel ${a?"open":""}">
        <div class="drawer-header">
          <h2>${R("notifications")} Notificações</h2>
          <button type="button" class="icon-btn" data-action="close-drawer">${R("close")}</button>
        </div>
        <div class="drawer-body">
          ${C.length===0?`<div class="empty-state">${R("notifications_off")}<p>Sem notificações.</p></div>`:C.map(S=>`
                <div class="notif-card">
                  <strong>${R(S.icon)} ${E(S.title)}</strong>
                  <p>${E(S.body)}</p>
                  <time>${Pt(S.createdAt)}</time>
                </div>
              `).join("")}
        </div>
      </aside>
    `}function v(){const C=Ct(),S=C.filter(I=>I.status==="pending").length,L=C.filter(I=>I.status==="approved"||I.status==="prioritized").length,g=Je(),$=Ei.format(new Date);return`
      <div class="card-gradient" style="margin-top:8px">
        <p class="workspace-title" style="margin:0;font-size:1.15rem"><span>🧠</span> Olá, ${E(e.name)}!</p>
        <p style="margin:6px 0 0;opacity:0.92;font-size:0.88rem">A visão executiva do workspace de inovação.</p>
        <div class="row-2" style="margin-top:16px;gap:10px">
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">R$ 120k</div>
            <div style="font-size:0.74rem;opacity:0.92">investimento</div>
          </div>
          <div style="background:rgba(255,255,255,0.22);padding:12px;border-radius:14px">
            <div style="font-size:1.55rem;font-weight:700">32%</div>
            <div style="font-size:0.74rem;opacity:0.92">ROI consolidado</div>
          </div>
        </div>
      </div>
      <p class="section-title">⚡ Ações rápidas</p>
      <div class="row-2">
        <button type="button" class="quick-card" data-action="new-guideline"><span class="emoji-badge">📣</span><span>Nova diretriz</span></button>
        <button type="button" class="quick-card" data-action="suggestion"><span class="emoji-badge">📨</span><span>Enviar sugestão</span></button>
      </div>
      <div class="row-2" style="margin-top:12px">
        <button type="button" class="quick-card" data-action="collaborators"><span class="emoji-badge">👥</span><span>Colaboradores</span></button>
        <button type="button" class="quick-card" data-action="all-team"><span class="emoji-badge">🏆</span><span>Ver ranking</span></button>
      </div>
      ${Zi(`🏆 Mais ativos · ${$}`,g.length>3?"Ver todos":"","team-tab")}
      ${g.length===0?it("Sem registros de ideias neste mês.","emoji_events"):g.map((I,P)=>`
              <div class="idea-item rank-${P+1}">
                <strong>#${P+1} ${E(I.name)}</strong>
                <div class="meta">${I.submitted} ideias · ${I.approved} aprovadas</div>
              </div>
            `).join("")}
      <div class="row-2" style="margin-top:14px">
        ${Et("Ideias totais",String(C.length),"no portfólio")}
        ${Et("Aprovadas",String(L),S?`${S} pendentes`:"")}
      </div>
      <div class="card card-hoverable" style="margin-top:16px">
        <strong>📊 Pipeline de inovação</strong>
        ${g.length===0?'<p class="muted-text" style="margin-top:8px">Aguardando dados do mês.</p>':sa(g.slice(0,6).map(I=>I.submitted),g.slice(0,6).map(I=>(I.name||"").split(" ")[0]))}
      </div>
    `}function y(){const C=Nt();return`
      <p class="section-title">📚 Diretrizes estratégicas</p>
      <p class="muted-text" style="margin:-6px 0 12px">Crie, edite e remova as diretrizes que orientam gestores e operadores.</p>
      ${C.length===0?it("Publique a primeira diretriz da liderança.","menu_book"):C.map(S=>`
            <div class="card card-hoverable guideline-card" style="margin-bottom:10px">
              <span class="guideline-label">🧭 Diretriz da liderança</span>
              <strong>${E(S.title)}</strong>
              <p class="muted-text" style="margin:8px 0 0">${E(S.content)}</p>
              <div class="guideline-meta">${R("calendar_month")} ${E(S.authorName||"Liderança")} · ${Pt(S.createdAt)}</div>
              <div style="display:flex;gap:6px;margin-top:10px">
                <button type="button" class="chip-btn" data-action="edit-guideline" data-id="${S.id}">${R("edit")} Editar</button>
                <button type="button" class="chip-btn reject" data-action="delete-guideline" data-id="${S.id}">${R("delete")} Excluir</button>
              </div>
            </div>
          `).join("")}
      <button type="button" class="btn-primary" data-action="new-guideline" style="margin-top:16px">+ Nova diretriz</button>
    `}function b(){const C=s||{title:"",content:""},S=C.id?"Editar diretriz":"Nova diretriz";return`
      <div class="overlay-screen">
        ${Mt(S)}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Diretrizes aparecem para todos os colaboradores como orientação estratégica.</p>
          <form class="form-card" id="guideline-form">
            <label>Título</label>
            <input name="title" required minlength="3" value="${E(C.title||"")}" />
            <label>Conteúdo</label>
            <textarea name="content" required minlength="10">${E(C.content||"")}</textarea>
            ${o?`<p class="form-error">${E(o)}</p>`:""}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">${C.id?"Salvar alterações":"Publicar"}</button>
            </div>
          </form>
        </div>
      </div>
    `}function k(){return`
      <div class="overlay-screen">
        ${Mt("Sugestão da liderança")}
        <div class="screen-scroll">
          <p class="muted-text" style="margin:0 0 12px">Envie uma orientação personalizada a um operador.</p>
          <form class="form-card" id="suggestion-form">
            <label>Operador</label>
            <select name="target">
              ${Qt.map(C=>`
                <option value="${C.id}" data-email="${C.email}" data-name="${C.name}" ${C.id===l?"selected":""}>
                  ${E(C.name)} (${E(C.email)})
                </option>
              `).join("")}
            </select>
            <label>Mensagem</label>
            <textarea name="message" required minlength="10" placeholder="Escreva sua orientação..."></textarea>
            ${c?`<p class="form-error">${E(c)}</p>`:""}
            <div class="form-actions">
              <button type="button" class="btn-secondary" data-action="back">Cancelar</button>
              <button type="submit" class="btn-primary">Enviar</button>
            </div>
          </form>
        </div>
      </div>
    `}function T(){const C=Je(),S=ni(),L=Ei.format(new Date);return`
      <div class="overlay-screen">
        ${Mt("Equipe")}
        <div class="screen-scroll">
          <p class="section-title" style="margin-top:4px">Destaques de ${L}</p>
          ${C.length===0?it("Sem registros neste mês.","emoji_events"):C.map((g,$)=>`
                <div class="idea-item rank-${$+1}">
                  <strong>#${$+1} ${E(g.name)}</strong>
                  <div class="meta">${E(g.email||"")}</div>
                  <div class="meta">${g.submitted} no mês · ${g.approved} aprovadas</div>
                </div>
              `).join("")}
          <p class="section-title">Ranking acumulado</p>
          ${S.length===0?it("Sem dados.","groups"):S.map((g,$)=>`
                <div class="idea-item">
                  <strong>#${$+1} ${E(g.name)}</strong>
                  <div class="meta">${g.submitted} enviadas · ${g.approved} aprovadas</div>
                </div>
              `).join("")}
        </div>
      </div>
    `}function w(){const C=Je();return`
      <p class="section-title">Equipe ativa em ${Ei.format(new Date)}</p>
      <p class="muted-text" style="margin:-6px 0 12px">Acompanhe todos os colaboradores mockados e quem está movimentando a inovação neste mês.</p>
      ${C.length===0?it("Sem registros neste mês.","emoji_events"):C.map((L,g)=>`
            <div class="idea-item rank-${g+1}">
              <strong>#${g+1} ${E(L.name)}</strong>
              <div class="meta">${E(L.email||"")}</div>
              <div class="meta">${L.submitted} ideias · ${L.approved} aprovadas${L.submitted===0?" · sem envios neste mês":""}</div>
            </div>
          `).join("")}
      <button type="button" class="btn-primary" data-action="suggestion" style="margin-top:16px">${R("send")} Enviar sugestão</button>
    `}function D(){const C=Ct();return`
      <p class="section-title">Pipeline de ideias</p>
      <p class="muted-text" style="margin:-6px 0 12px">Acompanhe o status de todas as ideias do portfólio.</p>
      ${C.length===0?it("Sem ideias registradas ainda.","track_changes"):C.map(S=>`
            <div class="idea-item">
              <strong>${E(S.title)}</strong>
              <div class="meta">${R("person")} ${E(Fe(S))} · ${E(S.category)} · ${Pt(S.createdAt)}</div>
              <p style="font-size:0.85rem;color:var(--innovate-text-secondary);margin-top:8px">${E(S.description)}</p>
              <span class="status ${fi(S.status)}">${ui(S.status)}</span>
            </div>
          `).join("")}
    `}function A(){const C=Ct(),S=Nt(),L=er("leader");return d?`
        <div class="card-gradient" style="margin-top:8px;text-align:center">
          <div class="profile-avatar-wrap">
            <label class="profile-avatar-wrap" style="cursor:pointer">
              ${ge(e)}
              <input type="file" accept="image/*" data-action="avatar-file" hidden />
            </label>
            <button type="button" class="profile-avatar-camera" data-action="avatar-file-btn">${R("photo_camera")}</button>
          </div>
          <div class="avatar-colors">
            ${L.map(g=>`
              <button type="button" class="avatar-color-dot ${e.avatarColor===g?"selected":""}" data-color="${g}" style="background:${g}"></button>
            `).join("")}
          </div>
          <div class="profile-edit-fields" style="text-align:left;margin-top:12px">
            <label>Nome completo</label>
            <input id="profile-name" value="${E(e.fullName)}" />
            <label>E-mail</label>
            <input id="profile-email" type="email" value="${E(e.email)}" />
          </div>
          ${f?`<p class="form-error">${E(f)}</p>`:""}
          <div class="form-actions" style="margin-top:14px;justify-content:center">
            <button type="button" class="btn-secondary" data-action="profile-cancel">Cancelar</button>
            <button type="button" class="btn-primary" data-action="profile-save" style="width:auto;background:rgba(255,255,255,0.92);color:var(--accent-dark);box-shadow:none">Salvar</button>
          </div>
        </div>
      `:`
      <div class="card-gradient profile-header" style="margin-top:8px">
        <div class="profile-avatar-wrap">${ge(e)}</div>
        <strong>${E(e.fullName)}</strong>
        <p style="margin:4px 0 0;opacity:0.95;font-size:0.88rem">${E(e.email)}</p>
        <span class="role-pill">${Ce("leader")}</span>
        <div style="margin-top:14px">
          <button type="button" class="btn-secondary" data-action="profile-edit" style="background:rgba(255,255,255,0.95);color:var(--accent-dark);border:none">
            ${R("edit")} Editar perfil
          </button>
        </div>
      </div>
      <div class="profile-stats">
        ${Et("Ideias portfólio",String(C.length))}
        ${Et("Diretrizes",String(S.length))}
      </div>
      <div class="profile-insight-card">
        <strong>${R("insights")} Foco executivo</strong>
        <p>Use diretrizes curtas, mensuráveis e conectadas a ROI para guiar melhor gestores e operadores.</p>
      </div>
      <p class="muted-text" style="margin-top:8px;text-align:center">
        ${R("shield")} Acesso total: diretrizes, equipe, curadoria e analítico.
      </p>
      <button type="button" class="btn-primary btn-danger" data-action="logout" style="margin-top:16px">Sair</button>
    `}function O(){switch(i){case"home":return v();case"guidelines":return y();case"team":return w();case"tracking":return D();case"profile":return A();default:return""}}function _(){if(r==="guideline-form")return`<div class="phone-screen-inner">${b()}</div>`;if(r==="suggestion")return`<div class="phone-screen-inner">${k()}</div>`;if(r==="team")return`<div class="phone-screen-inner">${T()}</div>`;if(r==="collaborators")return`<div class="phone-screen-inner">${h().html()}</div>`;const C={home:"Executivo",guidelines:"Diretrizes",team:"Equipe",tracking:"Acompanhamento",profile:"Perfil"};return`
      <div style="position:relative;flex:1;display:flex;flex-direction:column;min-height:0">
        ${u()}
        ${Ki(C[i],{showBadge:po()})}
        <div class="screen-scroll">${O()}</div>
        ${Qi(Eo,i)}
      </div>
    `}return{html:_(),mount(C){const S=()=>{C.innerHTML=_(),L(C),yr(C)};m=S;const L=g=>{var I,P,M,U,ct,dt,at,Ot,z,G;if(r==="collaborators"){h().bind(g,S);return}(I=p==null?void 0:p.stop)==null||I.call(p),tr(g,N=>{i=N,r=null,S()}),(P=g.querySelector('[data-action="collaborators"]'))==null||P.addEventListener("click",()=>{h().reset(),r="collaborators",S()}),(M=g.querySelector('[data-action="all-team"]'))==null||M.addEventListener("click",()=>{i="team",S()}),(U=g.querySelector('[data-action="notifications"]'))==null||U.addEventListener("click",()=>{mo(),a=!0,S()}),g.querySelectorAll('[data-action="close-drawer"]').forEach(N=>N.addEventListener("click",()=>{a=!1,S()})),g.querySelectorAll('[data-action="new-guideline"]').forEach(N=>N.addEventListener("click",()=>{s={title:"",content:""},o="",r="guideline-form",S()})),g.querySelectorAll('[data-action="edit-guideline"]').forEach(N=>N.addEventListener("click",()=>{const V=Nt().find(K=>K.id===N.dataset.id);V&&(s={id:V.id,title:V.title,content:V.content},o="",r="guideline-form",S())})),g.querySelectorAll('[data-action="delete-guideline"]').forEach(N=>N.addEventListener("click",()=>{window.confirm("Excluir esta diretriz?")&&(io(N.dataset.id),S())})),(ct=g.querySelector("#guideline-form"))==null||ct.addEventListener("submit",N=>{N.preventDefault();const V=N.target,K=V.title.value.trim(),lt=V.content.value.trim();if(K.length<3||lt.length<10){o="Preencha título (3+) e conteúdo (10+).",S();return}s!=null&&s.id?eo(s.id,{title:K,content:lt}):to({title:K,content:lt,authorName:e.fullName}),s=null,r=null,i="guidelines",S()}),g.querySelectorAll('[data-action="suggestion"]').forEach(N=>N.addEventListener("click",()=>{r="suggestion",c="",S()})),(dt=g.querySelector("#suggestion-form"))==null||dt.addEventListener("submit",N=>{N.preventDefault();const V=N.target,K=V.target.selectedOptions[0],lt=V.message.value.trim();if(lt.length<10){c="Mensagem com no mínimo 10 caracteres.",S();return}mn({senderName:e.fullName,senderRole:"leader",targetId:K.value,targetEmail:K.dataset.email,targetName:K.dataset.name,message:lt}),l=K.value,r=null,i="home",S()}),g.querySelectorAll('[data-action="team-tab"]').forEach(N=>N.addEventListener("click",()=>{r="team",S()})),g.querySelectorAll('[data-action="back"]').forEach(N=>N.addEventListener("click",()=>{r=null,o="",c="",S()})),(at=g.querySelector('[data-action="profile-edit"]'))==null||at.addEventListener("click",()=>{d=!0,f="",S()}),(Ot=g.querySelector('[data-action="profile-cancel"]'))==null||Ot.addEventListener("click",()=>{d=!1,e=di({...e,email:e.accountKey}),S()}),(z=g.querySelector('[data-action="profile-save"]'))==null||z.addEventListener("click",()=>{var lt,Vt,$e,q;const N=((Vt=(lt=g.querySelector("#profile-name"))==null?void 0:lt.value)==null?void 0:Vt.trim())||"",V=((q=($e=g.querySelector("#profile-email"))==null?void 0:$e.value)==null?void 0:q.trim())||"";if(N.length<2||!V.includes("@")){f="Dados inválidos.",S();return}const K=Sr(e.accountKey,{fullName:N,email:V,avatar:e.avatar,avatarColor:e.avatarColor});e={...e,...K},d=!1,f="",S()}),g.querySelectorAll("[data-color]").forEach(N=>{N.addEventListener("click",()=>{e.avatarColor=N.dataset.color,e.avatar=null,S()})});const $=g.querySelector('[data-action="avatar-file"]');(G=g.querySelector('[data-action="avatar-file-btn"]'))==null||G.addEventListener("click",()=>$==null?void 0:$.click()),$==null||$.addEventListener("change",()=>{var K;const N=(K=$.files)==null?void 0:K[0];if(!N)return;const V=new FileReader;V.onload=()=>{e.avatar=V.result,S()},V.readAsDataURL(N)}),g.querySelectorAll('[data-action="logout"]').forEach(N=>N.addEventListener("click",t))};S()}}}const Wi=document.getElementById("app");Sn();Wi.addEventListener("click",n=>{var e,i;((i=(e=n.target).closest)==null?void 0:i.call(e,'[data-action="theme-toggle"]'))&&kn()});function Ge(n){Wi.innerHTML=`<div class="phone-screen-inner">${n.html}</div>`;const t=Wi.firstElementChild;n.mount&&n.mount(t),Gs(t)}function _n(){Cr(null);const n=Ks(t=>{const e=di(t);switch(Cr(t.role),t.role){case"operator":Ge($o(e,Ti));break;case"manager":Ge(ko(e,Ti));break;case"leader":Ge(To(e,Ti));break}});Ge(n)}function Ti(){_n()}_n();
