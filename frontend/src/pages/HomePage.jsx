import { useEffect, useRef, useState } from 'react'
import { NavLink } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import {
  Zap,
  ShieldCheck,
  ScrollText,
  Users,
  ClipboardList,
  CheckCircle,
  Sword,
  Copy,
  Check,
  Plus,
} from 'lucide-react'
import { apiFetch } from '../api'
import { HeroModel } from '../components/HeroModel'
import './HomePage.css'

const EASE = [0.22, 1, 0.36, 1]

const fadeUp = {
  hidden: { opacity: 0, y: 32 },
  show: (i = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, delay: i * 0.12, ease: EASE },
  }),
}

const fadeUpSmall = {
  hidden: { opacity: 0, y: 20 },
  show: (i = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: 0.45, delay: i * 0.07, ease: EASE },
  }),
}

/* ============================= HERO ============================= */

function HeroContent() {
  return (
    <div className="home-hero-content">
      <motion.h1
        variants={fadeUp}
        initial="hidden"
        animate="show"
        custom={0}
        className="home-hero-title"
      >
        <span className="home-hero-title-line">Уникально?</span>
        <span className="home-hero-title-line">Ванильно?</span>
        <span className="home-hero-title-line home-accent">Ichorix</span>
      </motion.h1>

      <motion.p
        variants={fadeUp}
        initial="hidden"
        animate="show"
        custom={1}
        className="home-hero-desc"
      >
        Мы - классический Minecraft-сервер с активным игровым процессом, уютной атмосферой
        и дружелюбным сообществом, где каждый игрок может раскрыть свой потенциал
        и взаимодействовать с другими участниками. Строй, выживай, общайся и развивайся вместе с нами.
      </motion.p>

      <motion.div
        variants={fadeUp}
        initial="hidden"
        animate="show"
        custom={2}
        className="home-hero-actions"
      >
        {/* TODO: уточнить у команды правильный URL (возможно Discord-инвайт) */}
        <NavLink to="/access" className="home-btn home-btn-outline home-btn-lg">
          Подать заявку
        </NavLink>
        <NavLink to="/wiki" className="home-btn home-btn-ghost home-btn-lg">
          Вики
        </NavLink>
      </motion.div>
    </div>
  )
}

function HeroSection() {
  const dragState = useRef({
    active: false,
    lastX: 0,
    lastY: 0,
    velX: 0,
    velY: 0,
    rotX: 0,
    rotY: 0,
  })

  return (
    <section className="home-hero">
      <div className="home-hero-grid" />
      <div className="home-hero-vignette" />

      <div className="home-hero-inner">
        <HeroContent />

        <motion.div
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.3, ease: EASE }}
          className="home-hero-canvas"
          onMouseDown={(e) => {
            const ds = dragState.current
            ds.active = true
            ds.lastX = e.clientX
            ds.lastY = e.clientY
            ds.velX = 0
            ds.velY = 0
          }}
          onMouseMove={(e) => {
            const ds = dragState.current
            if (!ds.active) return
            ds.velX = (e.clientY - ds.lastY) * 0.002
            ds.velY = (e.clientX - ds.lastX) * 0.002
            ds.lastX = e.clientX
            ds.lastY = e.clientY
          }}
          onMouseUp={() => {
            dragState.current.active = false
          }}
          onMouseLeave={() => {
            dragState.current.active = false
          }}
          onTouchStart={(e) => {
            const ds = dragState.current
            ds.active = true
            ds.lastX = e.touches[0].clientX
            ds.lastY = e.touches[0].clientY
            ds.velX = 0
            ds.velY = 0
          }}
          onTouchMove={(e) => {
            const ds = dragState.current
            if (!ds.active) return
            ds.velX = (e.touches[0].clientY - ds.lastY) * 0.002
            ds.velY = (e.touches[0].clientX - ds.lastX) * 0.002
            ds.lastX = e.touches[0].clientX
            ds.lastY = e.touches[0].clientY
          }}
          onTouchEnd={() => {
            dragState.current.active = false
          }}
        >
          <div className="home-hero-glow home-hero-glow-1" />
          <div className="home-hero-glow home-hero-glow-2" />
          <div className="home-hero-glow home-hero-glow-3" />
          <HeroModel dragState={dragState} />
        </motion.div>
      </div>
    </section>
  )
}

/* ============================= FEATURES ============================= */

const featureCards = [
  {
    title: 'Уникально',
    description:
      'Уникальный геймплей с рп механиками, которого не найдёшь больше нигде. Здесь нет границ - только твои идеи и стремление воплощать их в жизнь.',
    showDetails: true,
    wide: true,
    icon: Zap,
  },
  {
    title: 'Стабильно',
    description: 'Сервер работает без перебоев - никаких вайпов или даунтаймов.',
    showDetails: false,
    icon: ShieldCheck,
  },
  {
    title: 'РП',
    description:
      'РП и сюжет - наше всё. Заходи, создавай героя и становись частью истории, которую пишем мы все вместе.',
    showDetails: false,
    icon: ScrollText,
  },
  {
    title: 'Доступность',
    description:
      'Зайти к нам можно бесплатно - просто оставь заявку в Дискорде, и мы тебя рассмотрим. Если не хочется ждать - есть платная проходка, которая даёт мгновенный доступ на проект.',
    showDetails: false,
    wide: true,
    icon: Users,
  },
]

function BigCard({ card, index }) {
  const Icon = card.icon
  return (
    <motion.div
      variants={fadeUpSmall}
      initial="hidden"
      whileInView="show"
      viewport={{ once: true, margin: '-60px' }}
      custom={index}
      className={`home-feat-card home-card-hover${card.wide ? ' home-feat-card-wide' : ''}`}
    >
      <div className="home-feat-card-body">
        <div className="home-feat-card-head">
          <Icon size={16} strokeWidth={1.8} className="home-feat-icon" />
          <span className="home-feat-title">{card.title}</span>
        </div>
        <span className="home-feat-desc">{card.description}</span>
      </div>

      {card.showDetails && (
        <div className="home-feat-card-foot">
          <NavLink to="/wiki" className="home-btn home-btn-ghost home-btn-sm">
            Вики
          </NavLink>
        </div>
      )}
    </motion.div>
  )
}

function TallCard() {
  return (
    <motion.div
      variants={fadeUpSmall}
      initial="hidden"
      whileInView="show"
      viewport={{ once: true, margin: '-60px' }}
      custom={2}
      className="home-feat-card home-card-hover home-tall-card"
    >
      <div className="home-tall-card-inner">
        <div className="home-feat-card-body">
          <span className="home-tall-title">Коммюнити</span>
          <span className="home-feat-desc">
            Тёплое и живое сообщество, где каждый чувствует себя своим.
            Никакой токсичности - только общение, совместные идеи и люди,
            которым не всё равно.
          </span>
        </div>
        {/* TODO: уточнить у команды правильный URL (возможно Discord-инвайт) */}
        <NavLink to="/access" className="home-btn home-btn-outline home-btn-sm">
          Играть сейчас
        </NavLink>
      </div>
    </motion.div>
  )
}

function FeaturesSection() {
  return (
    <section className="home-section home-features">
      <div className="home-section-hr" />
      <div className="home-section-inner">
        <motion.h2
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-80px' }}
          transition={{ duration: 0.5, ease: EASE }}
          className="home-features-title"
        >
          Почему именно
          <br />
          <span className="home-accent">Ichorix</span>
        </motion.h2>

        <div className="home-features-grid">
          {featureCards.map((card, i) => (
            <BigCard key={card.title} card={card} index={i} />
          ))}
          <TallCard />
        </div>
      </div>
    </section>
  )
}

/* ============================= HOW IT WORKS ============================= */

const steps = [
  {
    icon: ClipboardList,
    title: 'Подай заявку',
    description:
      'Заполни зявку в нашем Discord или купи проходку - это помогает нам отсеивать гриферов и делать классный сервер!',
    actions: [{ type: 'link', label: 'Подать заявку', to: '/access', variant: 'outline' }],
  },
  {
    icon: CheckCircle,
    title: 'Получи принятие заявки',
    description:
      'Наша модерация рассмотрит твою заявку в теч. 24 часов, если что она может задать доп. вопросы - так что будь всегда на связи!',
    actions: [],
  },
  {
    icon: Sword,
    title: 'Заходи и играй',
    description:
      'Заходи на сервер, знакомься с комьюнити и изучай фишки на вики и делай свой вклад в историю проекта!',
    actions: [
      { type: 'copy', label: 'play.ichorix.cc', value: 'play.ichorix.cc', variant: 'outline' },
      { type: 'link', label: 'Вики', to: '/wiki', variant: 'ghost' },
    ],
  },
]

function CopyButton({ value, label }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = () => {
    navigator.clipboard.writeText(value)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <button onClick={handleCopy} className="home-btn home-btn-outline home-btn-sm">
      {copied ? <Check size={12} className="home-btn-ico" /> : <Copy size={12} className="home-btn-ico" />}
      {copied ? 'Скопировано' : label}
    </button>
  )
}

function StepCard({ step, index }) {
  const Icon = step.icon
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-40px' }}
      transition={{ duration: 0.45, delay: index * 0.08, ease: EASE }}
      className="home-step-card home-card-hover"
    >
      <div className="home-step-head">
        <Icon size={16} className="home-step-icon" />
        <span className="home-step-title">{step.title}</span>
      </div>

      <p className="home-step-desc">{step.description}</p>

      {step.actions.length > 0 && (
        <div className="home-step-actions">
          {step.actions.map((action, i) =>
            action.type === 'copy' ? (
              <CopyButton key={i} value={action.value} label={action.label} />
            ) : (
              <NavLink
                key={i}
                to={action.to}
                className={`home-btn home-btn-${action.variant} home-btn-sm`}
              >
                {action.label}
              </NavLink>
            )
          )}
        </div>
      )}
    </motion.div>
  )
}

function HowItWorksSection() {
  return (
    <section className="home-section home-hiw">
      <div className="home-section-inner home-hiw-inner">
        <div className="home-hiw-head">
          <motion.h2
            initial={{ opacity: 0, y: 14 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.45, ease: EASE }}
            className="home-hiw-title"
          >
            Как попасть
            <br />
            <span className="home-accent">на сервер?</span>
          </motion.h2>
        </div>

        <div className="home-hiw-grid">
          {steps.map((step, i) => (
            <StepCard key={i} step={step} index={i} />
          ))}
        </div>
      </div>
    </section>
  )
}

/* ============================= CTA ============================= */

function CtaSection() {
  return (
    <section className="home-section home-cta">
      <div className="home-section-inner">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-40px' }}
          transition={{ duration: 0.5, ease: EASE }}
          className="home-cta-box"
        >
          <div className="home-cta-edge home-cta-edge-left" />
          <div className="home-cta-edge home-cta-edge-right" />

          <div className="home-cta-text">
            <motion.h2
              initial={{ opacity: 0, y: 14 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.45, delay: 0.1, ease: EASE }}
              className="home-cta-title"
            >
              Готов начать
              <br />
              <span className="home-accent">играть?</span>
            </motion.h2>

            <motion.p
              initial={{ opacity: 0, y: 10 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.4, delay: 0.18, ease: EASE }}
              className="home-cta-desc"
            >
              Подай заявку или купи проходку - и уже сегодня окажись на сервере
              вместе с живым комьюнити игроков.
            </motion.p>
          </div>

          <motion.div
            initial={{ opacity: 0, y: 10 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.4, delay: 0.26, ease: EASE }}
            className="home-cta-actions"
          >
            <NavLink to="/access" className="home-btn home-btn-primary home-btn-md">
              Подать заявку
            </NavLink>
            <NavLink to="/access" className="home-btn home-btn-outline home-btn-md">
              Купить проходку
            </NavLink>
          </motion.div>
        </motion.div>
      </div>
    </section>
  )
}

/* ============================= FAQ ============================= */

const faqs = [
  {
    question: 'На какой версии работает сервер?',
    answer: 'Сервер работает на 1.21.11, но мы при возможности всегда обновляем версию!',
  },
  {
    question: 'Нужна ли лицензия?',
    answer: 'Нет, для входа не нужна лицензия! Надо всего лишь купить проходку или заполнить заявку.',
  },
  {
    question: 'Когда стартовал сезон?',
    answer: 'Сезон стартовал 21 марта 2026 года.',
  },
  {
    question: 'Как попасть на сервер?',
    answer:
      'Чтобы попасть на сервер, вы можете подать заявку или купить проходку! Это помогает нам отсеивать большую часть гриферов.',
  },
  {
    question: 'Можно играть с телефона?',
    answer:
      'Только с PojavLauncher или других эмуляторов Java-версии Minecraft на телефон, с Bedrock нельзя зайти.',
  },
  {
    question: 'Какой онлайн на сервере?',
    dynamic: 'online',
    answer: null,
  },
  {
    question: 'А если меня убьют/загриферят?',
    answer:
      'Если такое случится, модерация всегда готова вам помочь и наказать нарушителя. Надо всего лишь открыть тикет в поддержку.',
  },
]

function FaqItem({ question, answer, index, isOpen, onToggle }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-40px' }}
      transition={{ duration: 0.45, delay: index * 0.06, ease: EASE }}
    >
      <button
        onClick={() => onToggle(index)}
        className={`home-faq-btn${isOpen ? ' home-faq-btn-open' : ''}`}
      >
        <span className="home-faq-q">{question}</span>
        <span className="home-faq-plus">
          <Plus size={14} className="home-faq-plus-ico" />
        </span>
      </button>

      <AnimatePresence initial={false}>
        {isOpen && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="home-faq-answer"
          >
            <div className="home-faq-answer-inner">
              <p className="home-faq-a">{answer}</p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  )
}

function FaqSection() {
  const [openIndex, setOpenIndex] = useState(null)
  const [online, setOnline] = useState(null)

  useEffect(() => {
    apiFetch('/web/server-stats')
      .then((d) => setOnline(d.online))
      .catch(() => {})
  }, [])

  const handleToggle = (index) => {
    setOpenIndex(openIndex === index ? null : index)
  }

  const resolveAnswer = (faq) => {
    if (faq.dynamic === 'online') {
      return online === null ? 'Загрузка...' : `Сейчас на сервере ${online} игроков.`
    }
    return faq.answer
  }

  return (
    <section className="home-section home-faq">
      <div className="home-section-inner home-faq-inner">
        <div className="home-faq-layout">
          <div className="home-faq-aside">
            <motion.h2
              initial={{ opacity: 0, y: 14 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.45, ease: EASE }}
              className="home-faq-title"
            >
              Частые
              <br />
              <span className="home-accent">вопросы</span>
            </motion.h2>
            <motion.p
              initial={{ opacity: 0, y: 10 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.4, delay: 0.1, ease: EASE }}
              className="home-faq-lead"
            >
              Тут вы найдете ответы на самые популярные вопросы. Если не нашли нужного ответа,
              не стесняйтесь обращаться к нам в Discord!
            </motion.p>
          </div>

          <div className="home-faq-list">
            {faqs.map((faq, i) => (
              <FaqItem
                key={i}
                question={faq.question}
                answer={resolveAnswer(faq)}
                index={i}
                isOpen={openIndex === i}
                onToggle={handleToggle}
              />
            ))}
          </div>
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-40px' }}
          transition={{ duration: 0.5, ease: EASE }}
          className="home-faq-cta"
        >
          <div className="home-faq-cta-text">
            <span className="home-faq-cta-title">Остались вопросы?</span>
            <span className="home-faq-cta-sub">
              Вы всегда можете обратиться к нам в Discord, мы ответим на ваш вопрос!
            </span>
          </div>

          {/* TODO: уточнить у команды правильный URL (возможно Discord-инвайт) */}
          <NavLink to="/access" className="home-btn home-btn-outline home-btn-md">
            Написать в Discord
          </NavLink>
        </motion.div>
      </div>
    </section>
  )
}

/* ============================= PAGE ============================= */

export default function HomePage() {
  return (
    <div className="home-root">
      <HeroSection />
      <FeaturesSection />
      <HowItWorksSection />
      <CtaSection />
      <FaqSection />
    </div>
  )
}
